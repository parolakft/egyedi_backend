// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.ejb.LabelDao;
import hu.parola.xy.ejb.VoteDao;
import hu.parola.xy.entity.Vote;
import hu.parola.xy.entity.VotesLabel;
import hu.parola.xy.enums.VoteValue;

/**
 * "Szemantikus címkéző algoritmus"
 */
@Singleton
@Lock(LockType.READ)
public class Labeller implements LabellerTask {

	@PersistenceContext
	private EntityManager em;
	@EJB
	private VoteDao votes;
	@EJB
	private LabelDao labels;

	/// fut-e a szinkron feladat...
	private boolean running;
	/// módosító user
	public final StringBuilder LOG = new StringBuilder(1000);

	//

	public Labeller() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Labeller.class);

	//

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public String getLog() {
		return String.format("<!-- %d -->%n%s", System.currentTimeMillis(), LOG);
	}

	private void log(final String msg) {
		LOG.append(String.format("<!-- %tF %<tT --> %s%n", System.currentTimeMillis(), msg));
		if (LOGGER.isInfoEnabled())
			LOGGER.info(msg.replaceAll("<[^>]+>", ""));
	}

	/// Lekérdezések
	private static final String JPQL = "SELECT v.id  FROM Vote v  WHERE v.value > 0 AND v.value <> ?2 AND v.created < ?1"
			+ " AND v NOT IN (SELECT vl.vote  FROM VotesLabel vl)";

	private int i = 0;

	/**
	 * A "szemantikus címkéző algoritmus" belépési pontja.
	 * <p>
	 * Lényegében végigmegy azokon a szavazatokon, amelyek még nem lettek
	 * felcímkézve, és generál nekik címkéket.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		LOG.setLength(0);
		log("<h2>Inicializálás</h2>");
		running = true;
		i = 0;

		// Algoritmusok
		Algorithm.resetAll(labels);
		try {
			log("<h3>Lekérdezés</h3>");
			final List<Integer> lista = em.createQuery(JPQL).setParameter(1, LocalDateTime.now().minusMinutes(30))
					.setParameter(2, VoteValue.UNKNOWN).setMaxResults(500).getResultList();
			final int l = null == lista ? 0 : lista.size();
			String msg = "Címkézendő szavazatok: " + l;
			log(msg);

			if (l > 0) {
				log("<h3>Folyamat indítása...</h3>");
				LOGGER.debug("Ciklus...");
				// Végigiterálunk a kapott ID listán, és egyenként betöltjük és felcímkézzük...
				/// Ketté lett vágva: párhuzamosan csak a címkék létrehozását csináljuk...
				lista.stream().map(votes::loadVote).flatMap(this::label).forEach(labels::label);
//				/// Majd sorosan a letárolást...
//				final int ll = l * Algorithm.count();
//				VotesLabel label = null;
//				while (i < ll || null != label) {
//					label = QUEUE.poll();
//					if (null != label)
//						labels.label(label);
//				}

			}
		} catch (Exception ex) {
			log("<h1>HIBA: " + ex + " </h1>");
			LOGGER.error("Labelling Error", ex);
		}
		running = false;
		log("<h3>Folyamat vége.</h3>");
	}

	/**
	 * Egy szavazat felcímkézése.
	 * 
	 * @param v Szavazat
	 * @return Címkék {@link Stream}e
	 */
	private Stream<VotesLabel> label(final Vote v) {
		log("<span style=\"display:inline-block; padding:4px;border: 1px solid silver;\">Vote " + (++i) + ": ID="
				+ v.getId() + "</span>");

		// Címkék generálása: Algoritmusonként... + Letárolás
		return Algorithm.generate(v);
	}

}
