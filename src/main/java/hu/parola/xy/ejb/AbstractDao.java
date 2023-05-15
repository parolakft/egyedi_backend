// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.entity.IEntity;
import hu.parola.xy.entity.User;

/**
 * Data Access Object = Adatelérési objektum,<br/>
 * lényegében egy köztes réteg a program és az adatbázis (kezelő) réteg között.
 * 
 * @param <T> Entitás osztály
 */
public abstract class AbstractDao<T extends IEntity> {

	protected final Class<T> entityClass;

	@PersistenceContext
	protected EntityManager em;

	//

	/**
	 * Konstruktor
	 * 
	 * @param entityClass Az entitás osztály példánya
	 */
	protected AbstractDao(final Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDao.class);

	// Közös metódusok

	/**
	 * Letárol egy új példányt, mind a cache-be, mind az adatbázisba, ha kell
	 * 
	 * @param entity A példány
	 */
	public void create(final T entity) {
		em.persist(entity);
	}

	/**
	 * Megfrissíti a cache-ben, illetve az adatbázisban található példányt a
	 * kapottal, és visszaadja a megfrissített értéket
	 * 
	 * @param entity A példány
	 * @return Az új példány
	 */
	public T save(final T entity) {
		return em.merge(entity);
	}

	/**
	 * Töröl egy példányt a cache-ből és az adatbázisból
	 * 
	 * @param entity Törlendő példány
	 */
	public void remove(final T entity) {
		em.remove(em.merge(entity));
	}

	/**
	 * Töröl egy példányt a cache-ből és az adatbázisból
	 * 
	 * @param entity Törlendő példány ID-ja. Ha nem található ilyen ID-val példány,
	 *               nem csinál semmit.
	 */
	public void remove(final Integer id) {
		final T item = em.find(entityClass, id);
		if (null != item)
			em.remove(item);
	}

	/**
	 * Betölt egy példányt, az ID alapján
	 * 
	 * @param id Betöltendő példány ID-je
	 * @return Betöltött példány
	 */
	public T load(final Integer id) {
		final T t = em.find(entityClass, id);
		if (null == t)
			throw new NoResultException(NOT_FOUND);
		return t;
	}

	/**
	 * Betölti az összes példányt
	 * 
	 * @return Betöltött példányok listája
	 */
	public List<T> listAll() {
		final CriteriaQuery<T> cq = em.getCriteriaBuilder().createQuery(entityClass);
		final Root<T> rt = cq.from(entityClass);
		cq.select(rt);
		final TypedQuery<T> q = em.createQuery(cq);
		return q.getResultList();
	}

	// Jogosultságkezelés

	private static final String HAS_CLASSES = "hasClasses: {}";

	/**
	 * Megmondja, hogy a {@code User} rendelkezik-e az adott nevű
	 * {@code Classes class}-szal.
	 * 
	 * @param user
	 * @param className
	 * @return
	 */
	public boolean hasClasses(final User user, final int className) {
		try {
			return user.getClasses().stream().anyMatch(classes -> className == classes.getType());
		} catch (final Exception ex) {
			LOGGER.warn(HAS_CLASSES, (Object) ex);
			return false;
		}
	}

	/**
	 * Megmondja, hogy a {@code User} rendelkezik-e az adott nevű
	 * {@code Classes class}-ok valamelyikével.
	 * 
	 * @param user
	 * @param className1
	 * @param className2
	 * @return
	 */
	public boolean hasClasses(final User user, final int className1, final int className2) {
		try {
			return user.getClasses().stream()
					.anyMatch(classes -> (className1 == classes.getType() || className2 == classes.getType()));
		} catch (final Exception ex) {
			LOGGER.warn(HAS_CLASSES, (Object) ex);
			return false;
		}
	}

	/**
	 * Megmondja, hogy a {@code User} rendelkezik-e az adott nevű
	 * {@code Classes class}-ok valamelyikével.
	 * 
	 * @param user
	 * @param classNames
	 * @return
	 */
	public boolean hasClasses(final User user, final int... classNames) {
		try {
			return user.getClasses().stream().anyMatch(
					classes -> (Arrays.stream(classNames).anyMatch(className -> className == classes.getType())));
		} catch (final Exception ex) {
			LOGGER.warn(HAS_CLASSES, (Object) ex);
			return false;
		}
	}

	// HibaÜzenet Konstansok

	public static final String INVALID_REQUEST = "INVALID_REQUEST";
	public static final String NO_AUTH = "NO_AUTH";
	public static final String INVALID_USER = "INVALID_USER";
	public static final String INACTIVE_USER = "INACTIVE_USER";
	public static final String NOT_FOUND = "NOT_FOUND";
	public static final String ALREADY_VOTED = "ALREADY_VOTED";
	public static final String ALREADY_REVIEWED = "ALREADY_REVIEWED";
	public static final String VOTE_LIMIT = "VOTE_LIMIT";
	public static final String FILE_ERROR = "FILE_ERROR";
	public static final String INVALID_ID = "INVALID_ID";
	public static final String PASSWORDS_OLD = "PASSWORDS_OLD";
	public static final String USER_IS_NOT_ACTIVE = "USER_IS_NOT_ACTIVE";
	public static final String MISSING_REGISTER_DATA = "MISSING_REGISTER_DATA";
	public static final String EMAIL_ALREADY_REGISTERED = "EMAIL_ALREADY_REGISTERED";
	public static final String INVALID_TOKEN = "INVALID_TOKEN";
	public static final String MISSING_FACEBOOK_DATA = "MISSING_FACEBOOK_DATA";
	public static final String MISSING_RESEND_DATA = "MISSING_RESEND_DATA";
	public static final String NO_RESEND_TOKEN = "NO_RESEND_TOKEN";

	//

	public <E> TypedQuery<E> createQuery(final String jpql, final Class<E> type) {
		return em.createQuery(jpql, type);
	}

	public Query createNativeQuery(final String sql) {
		return em.createNativeQuery(sql);
	}

}
