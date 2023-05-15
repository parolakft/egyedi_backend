// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.Tools;
import hu.parola.xy.dto.Item1Dto;
import hu.parola.xy.dto.Item2Dto;
import hu.parola.xy.dto.ListIdRequest;
import hu.parola.xy.dto.ListItemsRequest;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.Kind;

@Stateless
public class ItemDao extends AbstractDao<Item> {

	@EJB
	private MailerService mailerService;

	public ItemDao() {
		super(Item.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemDao.class);

	//

	/**
	 * Betölt egy példányt, az ID alapján
	 * 
	 * @param id Betöltendő példány ID-je
	 * @return Betöltött példány
	 */
	public Item load(final Integer id) {
		return em.createQuery("SELECT i  FROM Item i LEFT JOIN FETCH i.nutritions  WHERE i.id = ?1", entityClass)
				.setParameter(1, id).getSingleResult();
	}

	/**
	 * Betölt egy példányt, az ID alapján
	 * 
	 * @param id Betöltendő példány ID-je
	 * @return Betöltött példány
	 */
	public Item1Dto loadAll(final Integer id) {
		return new Item1Dto(load(id));
	}

	public List<Item2Dto> list(final ListIdRequest req) {
		final List<Item> lista = em
				.createQuery("SELECT DISTINCT i  FROM Item i  WHERE i.category.id = ?1 AND i.deleted = FALSE"
						+ "  ORDER BY i.name", Item.class)
				.setParameter(1, req.getId()).setFirstResult(req.getOffset()).setMaxResults(req.getLimit())
				.getResultList();
		final List<Item2Dto> listb = new ArrayList<>(lista.size());
		for (final Item item : lista) {
			listb.add(new Item2Dto(item));
		}
		return listb;
	}

	public List<Item> list(final ListItemsRequest req) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Item> cq = cb.createQuery(entityClass);
		final Root<Item> rt = cq.from(entityClass);
		rt.fetch("nutritions", JoinType.LEFT);
		cq.select(rt).distinct(true);
		final List<Predicate> predicates = new ArrayList<>();
		if (!Tools.isEmptyOrNull(req.getName())) {
			LOGGER.debug("name like {}", req.getName());
			predicates.add(cb.like(cb.upper(rt.get("name")), String.format("%%%S%%", req.getName())));
		}
		if (!Tools.isEmptyOrNull(req.getCats())) {
			LOGGER.debug("category in {}", req.getCats());
			predicates.add(rt.get("category").get("id").in(req.getCats()));
		}
		// TODO További szűrőfeltételek...
		if (!predicates.isEmpty())
			cq.where(predicates.toArray(Predicate[]::new));
		final TypedQuery<Item> q = em.createQuery(cq);
		// Limit
		if (Tools.isValidId(req.getLimit())) {
			LOGGER.debug("limit {}", req.getLimit());
			q.setMaxResults(req.getLimit());
		}
		return q.getResultList();
	}

	public void setImage(final Integer id, final User user, final File tmp) throws IOException {
		LOGGER.debug("in setImage {}, {}", id, tmp);
		final Item item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// XY-1015 Képkezelés
		final String ext = tmp.getName().replaceFirst("^.+\\.", ".");
		final File file = Tools.getTempPath(Kind.items, id, ext);
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
		final Item item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// XY-1015 Képkezelés
		final File file = Tools.getTempPath(Kind.items, id, item.getImage());
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
		final Item item = load(id);
		item.setDeleted(!item.isDeleted());
		item.init(musr);
		save(item);
	}

}
