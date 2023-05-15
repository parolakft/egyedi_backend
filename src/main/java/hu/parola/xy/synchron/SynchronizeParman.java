// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import static hu.parola.xy.enums.AllergenValue.NO;
import static hu.parola.xy.enums.AllergenValue.YES;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

import hu.parola.xy.CoderDecoder;
import hu.parola.xy.HttpPostClient;
import hu.parola.xy.dto.SyncRequest;
import hu.parola.xy.ejb.SettingDao;
import hu.parola.xy.entity.Allergen;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.ItemsAllergen;
import hu.parola.xy.entity.Norm;
import hu.parola.xy.entity.Nutrition;
import hu.parola.xy.entity.NutritionsDetails;

/**
 * Cikk-szinkronizálás: parMAN -> XY-egyedi
 * <p>
 * <b>{@code @Singleton} EJB</b> bean...
 * </p>
 */
@Singleton
@Lock(LockType.READ)
public class SynchronizeParman extends HttpPostClient implements SynchronizeParmanTask {

	@PersistenceContext
	private EntityManager em;
	@EJB
	private SettingDao cfgs;
	@Resource
	private ManagedExecutorService es;

	//

	/// fut-e a szinkron feladat...
	private boolean running;
	/// sikeres-e a szinkronizálás
	private boolean success;
	/// módosító user
	private String musr;
	/// parMAN API URL
	private String url;
	//
	private final StringBuilder LOG = new StringBuilder(1000);
	//
	private int i;
	//
	private final Queue<TAru> QUEUE = new ConcurrentLinkedQueue<>();

	/// default érték, az utolsó szikron ideje helyére, ha nincs a db-ben
	private static final LocalDateTime SINCE = LocalDateTime.of(2000, 1, 1, 0, 0);
	/// SETTINGS táblabeli azonosító
	private static final String LAST_TIME = "sync.last.time";
	/// parMAN oldalon a szerver címéhez adandó "context root"
	private static final String PARMAN_ROOT = "/api/";

	// XY-1002 Kiemelve ide, hogy ne fuson meg minden egyes termék
	// esetén...
	private List<NutritionsDetails> NUTS = null;
	private List<Allergen> ALLS = null;

	//

	/// Pédányosítás a konténer által
	public SynchronizeParman() {
		super();
	}

	/// Logger
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeParman.class);

	//

	/**
	 * @return Visszaadja, hogy fut-e szinkronizálás.
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public String getLog() {
		return String.format("<!-- %d -->%s", System.currentTimeMillis(), LOG);
	}

	private void log(final String msg) {
		LOG.append(String.format("<!-- %TF %<TT --> %s%n", System.currentTimeMillis(), msg));
	}

	/**
	 * Végrehajtja a szinkronizálást.
	 */
	@Override
	public void run() {
		LOG.setLength(0);
		log("<h2>Inicializálás</h2>");
		running = true;
		success = true;
		musr = null;
		QUEUE.clear();
		// XY-1002 Kiemelve ide, hogy ne fuson meg minden egyes termék
		// esetén...
		NUTS = em.createQuery("SELECT nd FROM NutritionsDetails nd", NutritionsDetails.class).getResultList();
		ALLS = em.createQuery("SELECT a FROM Allergen a", Allergen.class).getResultList();
		url = System.getProperty("xy.parman.url", "http://localhost:8080/BakeryServer/") + PARMAN_ROOT;
		i = 0;
		final LocalDateTime now = LocalDateTime.now();
		// Cikk szinkronizálása a parMAN rendszer felől...
		try {
			log("<h1>Az első request</h1>");
			// a kérések adattartalma
			final SyncRequest req = new SyncRequest();
			req.setFn("getXYCoolProducts");
			req.setId(null);
			// Kivesszük az adatbázisból, hogy mikortól kérdezzük le a cikkeket...
			LocalDateTime time = cfgs.getDateTime(LAST_TIME); // Default érték: 2000.01.01
			if (null == time)
				time = SINCE;
			req.setCoolDate(time);
			String msg = MessageFormat.format("{0}: {1}, {2}", req.getFn(), req.getSiteId(), req.getCoolDate());
			log("<p>" + msg + "</p>");
			LOGGER.debug(msg);
			// Elküldés
			final String resp = post(url, CoderDecoder.toJSON(req));
			final CoolResponse data = CoderDecoder.fromJSON(resp, CoolResponse.class);
			if (null != data.getError()) {
				if ("Bakery error, code: 404".equals(data.getError())) {
					log("<h2>Authorization failed!</h2>");
					LOGGER.error("Authorization failed!");
				} else
					LOGGER.error(data.getError());
			}
			// Ha nem történt hiba, (ami eddigre exceptiont dobott volna,) akkor itt
			// mindenképp van egy tömb, még ha üres is...
			final int l = data.getCoolIds().size();
			log("<p>válasz ID-k száma: " + l + "</p>");
			LOGGER.debug("coolIds: {}", l);
			if (l == 0) {
				log("<h1>Nincs mit szinkronizálni!</h1>");
				LOGGER.info("Nothing to synchronize!");
			}
			musr = "Synchron @ " + now;
			// Végigiterálunk a kapott ID listán, és egyenként beimportáljuk...
			/// Ketté lett vágva: párhuzamosan csak a parMAN lekérdezéseket csináljuk...
			es.submit(() -> data.getCoolIds().parallelStream().forEach(this::syncProduct));
			/// Majd sorosan az importot...
			while (i < l) {
				final TAru taru = QUEUE.poll();
				if (null != taru)
					syncTAru(taru);
				/// Ha üres a sor, de még lenne importálandó, várakozunk...
				// else Thread.sleep(10L);
			}

			LOGGER.info("Done synchronizing...");
			log("<h1>Szinkron vége</h1>");
		} catch (Exception ex) {
			log("<h1 style='color:red;'>HIBA: " + ex + " </h1>");
			LOGGER.error("Synchronize Error", ex);
			success = false;
		}
		try {
			LOGGER.debug("Success: {}", success);
			if (success) {
				cfgs.set(LAST_TIME, now.toString(), musr);
			}
		} catch (PersistenceException ex) {
			log("<h1>HIBA: " + ex + " </h1>");
			LOGGER.error("", ex);
		}
		running = false;
	}

	/// Lekérdez egyetlen cikket
	private void syncProduct(final Integer id) {
		try {
			// Második kérés összeállítása
			final SyncRequest req2 = new SyncRequest();
			req2.setFn("getProduct");
			req2.setId(id);
			LOGGER.trace("Sync {}", id);
			final String res = post(url, CoderDecoder.toJSON(req2));
			LOGGER.trace("JSON: {}", res);
			final TAru taru = CoderDecoder.fromJSON(res, TAru.class);
			// Beteszi a sorba
			QUEUE.offer(taru);
		} catch (Exception ex) {
			LOGGER.error("Synchronize Error: " + id, ex);
			success = false;
			++i;
		}
	}

	/// Beimportál egyetlen cikket
	private void syncTAru(final TAru taru) {
		++i;
		try {
			final Item item = findParman(taru.getTaruId(), taru.getArumn());
			log("<h2>Cikk: " + taru.getKod() + "</h2>");
			LOGGER.debug("{}. Item: {}, {}", i, item.getId(), item.getName());
			// DONE Konvertálás
			item.setCode(taru.getKod());
			item.setName(taru.getArumn());
			item.setShortName(taru.getShortName());
			item.setDescription(taru.getArumgj());
			item.setIngredients(taru.getAruosz());
			item.setDeleted(!taru.isMegjelenik());
			// Allergének, tápértékek, anyagnorma
			setAllergens(item, taru.getAllergenek());
			setTapertekek(item, taru.getTapertekek());
			setNormak(item, taru.getAlapanyagok());
			// Letárolás
			item.init(musr);
			em.merge(item);
		} catch (Exception ex) {
			LOGGER.error("Synchronize Error: " + taru.getTaruId(), ex);
			success = false;
		}
	}

	/// Kikeresi a parMAN ID alapján a cikket, ha nincs, létrehoz egy újat.
	private Item findParman(final Integer id, final String name) {
		try {
			// Keresés
			return em.createQuery("SELECT i FROM Item i WHERE i.parman = ?1", Item.class).setParameter(1, id)
					.getSingleResult();
		} catch (NoResultException ex) {
			LOGGER.debug("not found {}", id);
			// Új létrehozása
			final Item item = new Item();
			item.setParman(id);
			item.setName(name);
			item.setAllergens(new ArrayList<>());
			item.setNorm(new ArrayList<>());
			item.setNutritions(new ArrayList<>());
			item.init(musr);
			return em.merge(item);
		}
	}

	/// DONE Cikk allergénjeinek importja
	private void setAllergens(final Item item, final TAruAll alls) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		LOGGER.debug("Allergének...");
		final List<ItemsAllergen> lista = item.getAllergens();
		// all01-tól all14-ig vannak...
		for (final Allergen a : ALLS) {
			// final String id = String.format("all%02d", i); // Kód
			// final String mid = String.format("isAll%02d", i); // Getter metódus neve
			final String id = a.getCode(); // Kód
			final String mid = String.format("is%S%s", id.substring(0, 1), id.substring(1)); // Getter metódus neve
			// Kikeressük a példányt a kód alapján
			final ItemsAllergen ia = lista.stream().filter(all -> id.equals(all.getAllergen().getCode())).findAny()
					.orElse(null);
			final ItemsAllergen all;
			if (null != ia) {
				// Van példány
				all = ia;
			} else {
				// Nincs példány, létrehozzuk
				all = new ItemsAllergen();
				all.setItem(item);
				all.setAllergen(a);
			}
			// Módosítás, érték és letárolás
			final boolean value = (Boolean) TAruAll.class.getMethod(mid).invoke(alls);
			all.setInfo(value ? YES : NO);
			all.init(musr);
			em.merge(all);
		}
	}

	// DONE Cikk tápértékeinek importje
	private void setTapertekek(final Item item, final TAruTap taps) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		LOGGER.debug("Tápértékek...");
		// Tápértékek listája
		// Cikk tápértékeinek listája (halmaza)
		final List<Nutrition> set = item.getNutritions();
		// TAruTap osztály mezőinek listája
		final Field[] fields = TAruTap.class.getDeclaredFields();
		// Végigmegyünk a tápértékeken
		for (final NutritionsDetails nut : NUTS) {
			// Tápérték neve
			final String nm = nut.getName();
			// Kikeressük azt a mezőt, amelyen a @JsonProperty annotáció értéke megegyezik a
			// tápérték nevével...
			final Field field = Arrays.stream(fields).filter(f -> {
				final JsonProperty jp = f.getAnnotation(JsonProperty.class);
				return null != jp && jp.value().equals(nm);
			}).findAny().orElse(null);
			if (null == field) {
				// Ha nincs továbbállunk...
				LOGGER.warn("TAruTap field {} not found!", nm);
				continue;
			}
			// Mező- és getter neve
			final String fn = field.getName();
			final String getter = String.format("get%S%s", fn.substring(0, 1), fn.substring(1));
			// Meg is van... Megnézzük, szerepel-e a cikk tápértékei között
			final Nutrition n = set.stream().filter(nn -> nut.equals(nn.getDetail())).findAny().orElse(null);
			final Nutrition nnn;
			if (null != n) {
				// Szerepel, használjuk
				nnn = n;
			} else {
				// Nem szerepel, létrehozzuk
				nnn = new Nutrition();
				nnn.setItem(item);
				nnn.setDetail(nut);
			}
			// Módosítás, érték és letárolás
			final String value = (String) TAruTap.class.getMethod(getter).invoke(taps);
			nnn.setValue(value);
			nnn.init(musr);
			em.merge(nnn);
		}
	}

	// DONE Cikk anyagnormájának importja
	private void setNormak(final Item item, final List<TAruNorm> lista) {
		LOGGER.debug("Anyagnormák...");
		// XY-1061 Régi norma törlése
		em.createNativeQuery("DELETE  FROM public.material_norm  WHERE item_id = ?1").setParameter(1, item.getId())
				.executeUpdate();
		// Végigmegyünk a kapott listán
		// XY-1002 NPE-t dobált...
		if (null != lista && !lista.isEmpty())
			for (final TAruNorm n : lista) {
				// XY-1061 Felveszük új tételként
				final Norm norm = new Norm();
				norm.setItem(item);
				norm.setParman(n.getParManId());
				norm.setXyId(n.getXyId());
				// Idők, érték és letárolás
				norm.init(musr);
				norm.setValue(n.getValue());
				em.merge(norm);
			}
	}

}
