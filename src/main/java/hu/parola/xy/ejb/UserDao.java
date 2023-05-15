// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.Tools;
import hu.parola.xy.dto.ListUsersRequest;
import hu.parola.xy.entity.Allergen;
import hu.parola.xy.entity.Classes;
import hu.parola.xy.entity.User;
import hu.parola.xy.entity.UsersClasses;
import hu.parola.xy.enums.Kind;

@Stateless
public class UserDao extends AbstractDao<User> {

	@EJB
	private MailerService mailerService;

	public UserDao() {
		super(User.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);

	//

	/**
	 * Visszaadja az adott ID-val rendelkező kategóriát, ha nincs ilyen, a legelsőt
	 * a listából
	 * 
	 * @param id Betöltendő kategória ID-je
	 * @return Egy kategória
	 */
	public User login(String login, String password) {
		LOGGER.info("login: {}", login);
		if (Tools.isEmptyOrNull(login) || Tools.isEmptyOrNull(password)) {
			LOGGER.warn("Unspecified value");
			return null;
		}
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.email = ?1 AND u.password = ?2", User.class)
					.setParameter(1, login).setParameter(2, Tools.encodeMd5(password)).getSingleResult();
		} catch (final Exception ex) {
			// NOT FOUND, MULTI RESULT vagy bármi más...
			LOGGER.error("Login: " + ex);
			return null;
		}
	}

	public void lostPassword(String email) {
		LOGGER.info("lost passwd: {}", email);

		if (Tools.isEmptyOrNull(email)) {
			throw new IllegalArgumentException(INVALID_REQUEST);
		}

		User user = findByEmail(email);
		if (user == null) {
			throw new IllegalArgumentException(INVALID_USER);
		} else if (user.isActive()) {

			user.generateActivator();
			save(user);
			sendLostPasswordMail(user);
		}

		else if (!Tools.isEmptyOrNull(user.getToken())) {
			LOGGER.warn("Forgotten password: INACTIVE_USER");
			throw new IllegalArgumentException(INACTIVE_USER);
		}

	}

	public void sendActivatorMail(final User user) {

		final String url = System.getProperty("xy.egyedi.url", "http://localhost:3000/");
		final String body = fill("/mail/register.html", url + "admin/activate/" + user.getToken());
		final String[] pix = null;

		mailerService.send(user.getEmail(), null, null, "XY Egyedi rendszer - Regisztráció", body, pix);

	}

	public void sendLostPasswordMail(final User user) {

		final String url = System.getProperty("xy.egyedi.url", "http://localhost:3000/");
		final String body = fill("/mail/forgottenPassword.html", url + "admin/password/" + user.getToken());
		final String[] pix = null;

		mailerService.send(user.getEmail(), null, null, "XY Egyedi rendszer - Jelszóemlékeztető", body, pix);

	}

	public User findByEmail(String email) {
		try {
			return em.createQuery("select u from User u where u.email = ?1", User.class).setParameter(1, email)
					.getSingleResult();
		} catch (Exception ex) {
			LOGGER.info("findByEmail: {}", ex.getMessage());
			return null;
		}
	}

	public User findByFacebookId(String facebookId) {
		try {
			return em.createQuery("select u from User u where u.facebookId = ?1", User.class)
					.setParameter(1, facebookId).getSingleResult();
		} catch (Exception ex) {
			LOGGER.info("findByFacebookId: {}", ex.getMessage());
			return null;
		}
	}

	public User confirm(String token) {
		try {
			return em.createQuery("SELECT u FROM User u WHERE u.token = ?1", User.class).setParameter(1, token)
					.getSingleResult();
		} catch (Exception ex) {
			LOGGER.info("confirm: {}", ex.getMessage());
			return null;
		}
	}

	public String fill(final String resource, final Object... params) {

		final String res = Tools.load(getClass().getResourceAsStream(resource));
		for (int i = 0; i < params.length; ++i)
			if (null == params[i]) {
				params[i] = "";
			}
		return MessageFormat.format(res, params);
	}

	public void setAllergies(final Integer uid, final List<Integer> list) {
		final User user = load(uid);
		// Ellenőrzések: felhasználó és allergiák listája
		if (null == list)
			throw new IllegalArgumentException(INVALID_REQUEST);
		// Allergének listája, ezen fogunk végiggyalogolni...
		final List<Allergen> alls = em.createQuery("SELECT a FROM Allergen a ORDER BY a.order", Allergen.class)
				.getResultList();
		if (null == alls)
			return;
		// Felhasználó allergiáinak halmaza. Ha még nincs neki, példányosítunk egy üreset...
		Set<Allergen> allergies = user.getAllergies();
		if (null == allergies) {
			allergies = new HashSet<>();
			user.setAllergies(allergies);
		}
		// Végigmegyünk az allergéneken...
		for (final Allergen all : alls) {
			if (null == all)
				continue;
			// Ha érzékeny rá felvisszük, ha nem töröljük...
			if (list.contains(all.getId()))
				allergies.add(all);
			else
				allergies.remove(all);
		}
		// Letároljuk, ha még nem történt volna meg...
		save(user);
	}

	public List<User> list(final ListUsersRequest req) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<User> cq = cb.createQuery(entityClass);
		final Root<User> rt = cq.from(entityClass);
		cq.select(rt);
		final List<Predicate> predicates = new ArrayList<>();
		// Szűrés névre
		if (!Tools.isEmptyOrNull(req.getName())) {
			LOGGER.debug("name like {}", req.getName());
			predicates.add(cb.like(cb.upper(rt.get("name")), String.format("%%%S%%", req.getName())));
		}
		// Szűrés emailre
		if (!Tools.isEmptyOrNull(req.getEmail())) {
			LOGGER.debug("email like {}", req.getEmail());
			predicates.add(cb.like(cb.upper(rt.get("email")), String.format("%%%S%%", req.getEmail())));
		}
		// Esetleges további szűrőfeltételek...
		if (!predicates.isEmpty())
			cq.where(predicates.toArray(Predicate[]::new));
		final TypedQuery<User> q = em.createQuery(cq);
		// Limit
		if (Tools.isValidId(req.getLimit())) {
			LOGGER.debug("limit {}", req.getLimit());
			q.setMaxResults(req.getLimit());
		}
		return q.getResultList();
	}

	public void setClasses(final User u, final Integer uid, final List<Integer> list) {
		final User user = load(uid);
		// Ellenőrzések: felhasználó és osztályok listája
		if (null == list || null == user)
			throw new IllegalArgumentException(INVALID_REQUEST);
		// Osztályok listája, ezen fogunk végiggyalogolni...
		final List<Classes> clses = em.createQuery("SELECT c FROM Classes c", Classes.class).getResultList();
		if (null == clses)
			return;
		// Felhasználó osztályainak halmaza. Ha még nincs neki, példányosítunk egy üreset...
		Set<Classes> classes = user.getClasses();
		if (null == classes) {
			classes = new HashSet<>();
			user.setClasses(classes);
		}
		// Végigmegyünk az osztályokon...
		for (final Classes cls : clses) {
			if (null == cls)
				continue;
			// Ha rendelkezik vele, felvisszük, ha nem, töröljük...
			if (list.contains(cls.getId())) {
				final UsersClasses uc = new UsersClasses();
				uc.init(u);
				uc.setUser(user);
				uc.setClasses(cls);
				em.merge(uc);
			} else {
				em.createQuery("DELETE FROM UsersClasses uc WHERE uc.user = ?1 AND uc.classes = ?2")
						.setParameter(1, user).setParameter(2, cls).executeUpdate();
			}
		}
		// Letároljuk, ha még nem történt volna meg...
		save(user);
	}

	public void setImage(final Integer id, final User user, final File tmp) throws IOException {
		final User item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// XY-1015 Képkezelés
		final String ext = tmp.getName().replaceFirst("^.+\\.", ".");
		final File file = Tools.getTempPath(Kind.votes, id, ext);
		LOGGER.debug("move {} -> {}", tmp, file);
		file.delete(); // Felülírjuk az esetleg meglévőt...
		final boolean moved = tmp.renameTo(file);
		tmp.delete();
		if (!moved)
			throw new IOException(FILE_ERROR);
		item.init(user);
		item.setImage(ext);
		save(item);
	}

	public void removeImage(final Integer id, final User user) {
		final User item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// XY-1015 Képkezelés
		final File file = Tools.getTempPath(Kind.profiles, id, item.getImage());
		LOGGER.debug("remove {}", file);
		// Töröljük a fájlt...
		file.delete();
		// Töröljük a bejegyzést...
		item.init(user);
		item.setImage(null);
		save(item);
	}

	// XY-1014 Logikai törlés
	public void delete(Integer id, User musr) {
		final User user = load(id);
		user.setActive(!user.isActive());
		user.setToken(null);
		user.init(musr);
		save(user);
	}

}
