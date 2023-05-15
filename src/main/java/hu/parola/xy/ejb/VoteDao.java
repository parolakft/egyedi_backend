// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.Tools;
import hu.parola.xy.dto.ActivitiesRequest;
import hu.parola.xy.dto.VoteSync;
import hu.parola.xy.entity.Activity;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.User;
import hu.parola.xy.entity.Vote;
import hu.parola.xy.entity.VoteDetailed;
import hu.parola.xy.enums.ActKind;
import hu.parola.xy.enums.Kind;
import hu.parola.xy.enums.VoteValue;

@Stateless
public class VoteDao extends AbstractDao<Vote> {

	@EJB
	private MailerService mailerService;

	public VoteDao() {
		super(Vote.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VoteDao.class);

	//

	public Integer createEmpty(User user, Integer item) {

		// XY-1056 Szavazás lassú
		LOGGER.debug("user {} , item {}", user.getId(), item);
		final Integer id = (Integer) em
				.createNativeQuery("INSERT  INTO public.votes (vote_id, item_id, user_id, vote, voteftst, active)"
						+ "  VALUES (DEFAULT, ?1, ?2, 0, NOW(), TRUE)  RETURNING vote_id")
				.setParameter(1, item).setParameter(2, user.getId()).getSingleResult();
		LOGGER.debug("vote {}", id);
		return id;
	}

	public void cast(final Integer id, final VoteValue value) {
		// Betöltjük az ID alapján a vote példányt
		// XY-1056 Szavazás lassú
		final Object[] tomb = (Object[]) em.createNativeQuery(
				"SELECT vote_id, email, vote, MAX(vote_count), (SELECT COALESCE(COUNT(vote_id), 0)\\:\\:integer  FROM public.votes"
						+ "  WHERE user_id = u.user_id AND voteftst\\:\\:date = CURRENT_DATE AND vote <> 0)"
						+ "  FROM public.votes  JOIN public.users u USING (user_id)  LEFT JOIN public.users_classes USING(user_id)"
						+ "  LEFT JOIN public.classes USING (class_id)  WHERE vote_id = ?1  GROUP BY vote_id, u.user_id")
				.setParameter(1, id).getSingleResult();
		if (null == tomb[0])
			throw new NoResultException(NOT_FOUND);
		LOGGER.debug("user {}", tomb[1]);
		// Van-e már szavazva?
		if (!Integer.valueOf(0).equals(tomb[2]))
			throw new IllegalStateException(ALREADY_VOTED);
		// Hányat szavazhat egy nap? Ha nincs egy osztálya se, -1 -et!
		final int l1 = Tools.coalesce((Integer) tomb[3], -1);
		LOGGER.debug("hányat szavazhat? {}", l1);
		// Hányat szavazott már ma?
		final int l2 = Tools.coalesce((Integer) tomb[4], 0);
		LOGGER.debug("hányat szavazott? {}", l2);
		// Elérte-e a limitet? Ha nincs egy osztálya se, akkor nem szavazhat!
		if (0 == l1 || l2 < l1) {
			em.createNativeQuery("UPDATE public.votes SET vote = ?1, voteftst = CURRENT_TIMESTAMP  WHERE vote_id = ?2")
					.setParameter(1, value.ordinal()).setParameter(2, id).executeUpdate();
		} else
			throw new IllegalStateException(VOTE_LIMIT);
	}

	public void castDetailed(final Integer id, final Integer detail, final VoteValue value) {
		// XY-1056 Szavazás lassú
		final Object[] tomb = (Object[]) em.createNativeQuery(
				"SELECT vote_id,  (SELECT COUNT(vote_detail_value_id) > 0  FROM public.vote_details_value"
						+ "  WHERE vote_id = ?1 AND vote_detail_id = ?2), (SELECT vote_detail_id  FROM public.vote_details"
						+ "  WHERE vote_detail_id = ?2)  FROM public.votes  WHERE vote_id = ?1")
				.setParameter(1, id).setParameter(2, detail).getSingleResult();
		LOGGER.debug("vote {}, voted {}, detail {}", tomb);
		// Van-e szavazat illetve részlet?
		if (null == tomb[0] || null == tomb[2])
			throw new NoResultException(NOT_FOUND);
		// Van-e már ilyen?
		if (Boolean.TRUE.equals(tomb[1]))
			throw new IllegalStateException(ALREADY_VOTED);
		em.createNativeQuery(
				"INSERT  INTO public.vote_details_value (vote_detail_value_id, vote_detail_id, vote_id, vote_detail_value,"
						+ " vote_detail_valuectst)  VALUES (DEFAULT, ?1, ?2, ?3, CURRENT_TIMESTAMP)")
				.setParameter(1, detail).setParameter(2, id).setParameter(3, value.ordinal()).executeUpdate();
	}

	public List<VoteDetailed> listDetaileds() {
		return em.createQuery("SELECT d FROM VoteDetailed d WHERE d.active = TRUE ORDER BY d.order", VoteDetailed.class)
				.getResultList();
	}

	public void review(final Integer id, final String text) {
		// XY-1056 Szavazás lassú
		final Object[] tomb = (Object[]) em
				.createNativeQuery("SELECT vote_id, review"
						+ "  FROM public.votes  LEFT JOIN public.vote_reviews USING (vote_id)  WHERE vote_id = ?1")
				.setParameter(1, id).getSingleResult();
		LOGGER.debug("vote {}", tomb[0]);
		if (null == tomb[0])
			throw new NoResultException(NOT_FOUND);
		LOGGER.debug("review {}", tomb[1]);
		if (null != tomb[1])
			throw new IllegalStateException(ALREADY_REVIEWED);
		em.createNativeQuery("INSERT INTO public.vote_reviews (vote_review_id, vote_id, review, vote_reviewctst)"
				+ "  VALUES (DEFAULT, ?1, ?2, CURRENT_TIMESTAMP)").setParameter(1, id).setParameter(2, text)
				.executeUpdate();
	}

	public void setImage(final Integer id, final File tmp) throws IOException {
		// XY-1056 Szavazás lassú
		final Object[] tomb = (Object[]) em
				.createNativeQuery("SELECT vote_id, vote_img_id"
						+ "  FROM public.votes  LEFT JOIN public.vote_imgs USING (vote_id)  WHERE vote_id = ?1")
				.setParameter(1, id).getSingleResult();
		LOGGER.debug("vote {}", tomb[0]);
		if (null == tomb[0])
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
		if (null == tomb[1])
			em.createNativeQuery("INSERT  INTO public.vote_imgs (vote_img_id, vote_id, image, vote_imgctst)"
					+ "  VALUES (DEFAULT, ?1, ?2, CURRENT_TIMESTAMP)").setParameter(1, id).setParameter(2, ext)
					.executeUpdate();
		else
			em.createNativeQuery(
					"UPDATE public.vote_imgs  SET image = ?2, vote_imgctst = CURRENT_TIMESTAMP  WHERE vote_id = ?1")
					.setParameter(1, id).setParameter(2, ext).executeUpdate();
	}

	public List<Activity> list(final ActivitiesRequest req) {
		final StringBuilder sql = new StringBuilder(1000);
		// Cikk vagy termék
		if (Tools.isValidId(req.getId())) {
			LOGGER.debug("{} ID {}", req.getKind(), req.getId());
			sql.append(" AND (").append(req.getKind().name()).append("_id = :id)");
		}
		// Dátum intervallum
		if (null != req.getFrom()) {
			LOGGER.debug("from {}", req.getFrom());
			sql.append(" AND (voteftst >= :from)");
		}
		if (null != req.getTo()) {
			LOGGER.debug("to {}", req.getTo());
			sql.append(" AND (voteftst <= :to)");
		}
		// Ha nincsenek szűrőfeltételek, akkor üres listát adunk vissza...
		if (0 == sql.length())
			return Collections.emptyList();
		// Cikk esetén nem érdekel a megtekintés, csak az értékelés...
		if (ActKind.item.equals(req.getKind())) {
			sql.append(" AND (vote > 0)");
		}
		// Lekérdezés
		@SuppressWarnings("unchecked")
		final TypedQuery<Activity> q = (TypedQuery<Activity>) em.createNativeQuery(
				"SELECT vote_id, item_id, items.name AS item_name, items.image AS item_image, user_id, users.name AS user_name,"
						+ " users.image AS user_image, vote, voteftst AS created, review, vote_imgs.image  FROM votes  JOIN items USING (item_id)"
						+ "  JOIN users USING (user_id)  LEFT JOIN vote_reviews USING (vote_id)  LEFT JOIN vote_imgs USING (vote_id)  WHERE"
						+ sql.substring(4),
				Activity.class);
		// Paraméterek
		q.setParameter("id", req.getId()).setMaxResults(req.getLimit());
		// Dátum intervallum
		if (null != req.getFrom()) {
			q.setParameter("from", req.getFrom());
		}
		if (null != req.getTo()) {
			q.setParameter("to", req.getTo());
		}
		// Megfuttatás
		final List<Activity> result = q.getResultList();
		// Ha nem üres az eredménylista,
		if (null != result && !result.isEmpty())
			// Lekérdezzük a részletes értékeléseket,
			em.createQuery(
					"SELECT v.vote.id, v.detail.detail, v.value  FROM VoteDetailedValue v   WHERE v.vote.id IN ?1",
					// Az eredménylista ID-jei alapján...
					Object[].class).setParameter(1, result.stream().map(Activity::getId).collect(Collectors.toList()))
					// Végigmengyünk minden részletes értékelésen,
					.getResultStream().forEach(tomb -> {
						// És felvisszük az eredménylistabeli elemhez,
						result.stream().filter(a -> a.getId().equals(tomb[0]))
								// Mint <szempont> -> <értékelés> mappinget...
								.forEach(a -> a.getDetails().put((String) tomb[1], (VoteValue) tomb[2]));
					});
		// Visszaadjuk az eredménylistát, most már a részletes értékelésekkel együtt...
		return result;
	}

	//

	public Vote loadVote(final Integer id) {

		LOGGER.debug("loadVote: {}", id);
		try {
			final Object[] tomb = (Object[]) em.createNativeQuery(
					"SELECT vote_id, user_id, item_id, vote, voteftst, active  FROM public.votes  WHERE vote_id = ?1")
					.setParameter(1, id).getSingleResult();
			final Vote vote = new Vote();
			vote.setId((Integer) tomb[0]);
			final User user = new User();
			user.setId((Integer) tomb[1]);
			vote.setUser(user);
			final Item item = new Item();
			item.setId((Integer) tomb[2]);
			vote.setItem(item);
			// NULL kezelés
			final Integer i = (Integer) tomb[3];
			if (null != i)
				vote.setValue(VoteValue.values()[i]);
			vote.setCreated(((Timestamp) tomb[4]).toLocalDateTime());
			vote.setActive((Boolean) tomb[5]);
			return vote;
		} catch (final PersistenceException ex) {
			LOGGER.error("", ex);
			return null;
		}
	}

	//

	/**
	 * Központi rendszer felé történő szinkronizálás
	 * 
	 * @return Szinkronizálandó adatok listája
	 */
	public List<VoteSync> sync() {
		LOGGER.debug("syncVotes");
		return Collections.emptyList();
	}

}
