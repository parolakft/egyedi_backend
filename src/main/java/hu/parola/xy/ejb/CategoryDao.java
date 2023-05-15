// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.Tools;
import hu.parola.xy.entity.Category;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.Kind;

@Stateless
public class CategoryDao extends AbstractDao<Category> {

	@EJB
	private MailerService mailerService;

	public CategoryDao() {
		super(Category.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDao.class);

	//

	/**
	 * Kilistázza a fő- és alkategóriákat
	 * 
	 * @return Főkategóriák listája, mindegyik tartalmazza a saját alkategóriáit
	 */
	public List<Category> findAll() {
		LOGGER.trace("findAll");
		final List<Category> result = new ArrayList<>();
		final List<Category> list = em
				.createQuery("SELECT c  FROM Category c LEFT JOIN c.parent  ORDER BY c.parent.name NULLS FIRST, c.name",
						Category.class)
				.getResultList();
		if (null != list && !list.isEmpty()) {
			for (final Category al : list) {
				final Category fo = al.getParent();
				if (null == fo) {
					put(result, al);
				} else {
					put(result, fo);
					addChild(fo, al);
				}
			}
		}
		return result;
	}

	private <T> void put(List<T> list, T item) {
		if (null != list && null != item && !list.contains(item))
			list.add(item);
	}

	private static void addChild(Category parent, Category child) {
		var list = parent.getChildren();
		if (null == list) {
			list = new ArrayList<>();
			parent.setChildren(list);
		}
		list.add(child);
	}

	private void addItem(Category parent, Item item) {
		var list = parent.getItems();
		if (null == list) {
			list = new ArrayList<>();
			parent.setItems(list);
		}
		list.add(item);
	}

	/**
	 * Kilistázza a fő- és alkategóriákat, valamint a Cikkeket
	 * 
	 * @return Főkategóriák listája, mindegyik tartalmazza a saját alkategóriáit.
	 *         Mind a fő-, mind az alkategóriák tartalmazzák a saját cikkeiket.
	 */
	public List<Category> listWithItems() {
		LOGGER.trace("findAll");
		final List<Category> result = findAll();
		final List<Item> list = em.createQuery("SELECT i  FROM Item i ORDER BY i.name", Item.class).getResultList();
		if (null != list && !list.isEmpty())
			for (final Item i : list) {
				// Mivel az allergénekre, stb itt nem vagyunk kíváncsiak, ezeket kitöröljük. De
				// hogy mindez nem menjen vissza az adatbázisba, előtte detach()-oljuk...
				em.detach(i);
				i.setAllergens(null);
				i.setNutritions(null);
				// i.setNorm(null);
				loop(result, i);
			}
		return result;
	}

	private void loop(final List<Category> list, final Item item) {
		if (null == item || null == list || list.isEmpty())
			return;
		for (final Category cat : list) {
			if (null == cat)
				continue;
			final Category cc = item.getCategory();
			if (null != cc && Objects.equals(cat.getId(), cc.getId())) {
				addItem(cat, item);
				return;
			}
			if (null != cat.getChildren())
				loop(cat.getChildren(), item);
		}
	}

	public void setParent(final Integer cid, final Integer pid) {
		final Category cat = load(cid);
		if (null == cat)
			throw new IllegalStateException(INVALID_REQUEST);

		Category parent = null;
		if (null != pid) {
			parent = load(pid);
			if (null == parent)
				throw new IllegalStateException(INVALID_REQUEST);
		}
		cat.setParent(parent);
		save(cat);
	}

	public void setImage(final Integer id, final User user, final File tmp) throws IOException {
		final Category item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// DONE Pontos mappa-útvonal...
		final String ext = tmp.getName().replaceFirst("^.+\\.", ".");
		final File file = Tools.getTempPath(Kind.cats, id, ext);
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
		final Category item = load(id);
		if (null == item)
			throw new NoResultException(NOT_FOUND);
		// DONE Pontos mappa-útvonal...
		final File file = Tools.getTempPath(Kind.cats, id, item.getImage());
		LOGGER.debug("remove {}", file);
		// Töröljük a fájlt...
		file.delete();
		// Töröljük a bejegyzést...
		item.setImage(null);
		item.init(user);
		save(item);
	}

	/**
	 * Töröl egy példányt a cache-ből és az adatbázisból
	 * 
	 * @param entity Törlendő példány
	 */
	@Override
	public void remove(final Category entity) {
		removeChildren(em.merge(entity));
		em.remove(entity);
	}

	/**
	 * Töröl egy példányt a cache-ből és az adatbázisból
	 * 
	 * @param entity Törlendő példány ID-ja. Ha nem található ilyen ID-val példány,
	 *               nem csinál semmit.
	 */
	@Override
	public void remove(final Integer id) {
		final Category item = em.find(entityClass, id);
		if (null != item) {
			removeChildren(item);
			em.remove(item);
		}
	}

	private void removeChildren(Category fo) {
		if (null == fo)
			return;
		final List<Category> children = fo.getChildren();
		final List<Item> items = fo.getItems();
		if (null != children && !children.isEmpty())
			for (final Category child : children) {
				child.setParent(null);
				child.setParentId(null);
				em.merge(child);
			}
		else if (null != items && !items.isEmpty())
			for (final Item item : items) {
				item.setCategory(null);
				item.setCategoryId(null);
				em.merge(item);
			}
	}

	// XY-1014 Logikai törlés
	public void delete(Integer id, User musr) {
		final Category cat = load(id);
		cat.setDeleted(!cat.isDeleted());
		cat.init(musr);
		save(cat);

		removeChildren(cat);
	}

}
