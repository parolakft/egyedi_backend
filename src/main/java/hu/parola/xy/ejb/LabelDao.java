// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.Label;
import hu.parola.xy.entity.VotesLabel;

@Stateless
public class LabelDao extends AbstractDao<Label> {

	@EJB
	private MailerService mailerService;

	//

	public LabelDao() {
		super(Label.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(LabelDao.class);

	//

	// XY-1018 Szemanikus címkéző

	// Letároljuk az adatbázisba...
	public void label(final VotesLabel label) {
		em.createNativeQuery(
				"INSERT  INTO public.votes_labels (vote_id, vote_labelctst, full_label)  VALUES (?1, ?2, ?3)")
				.setParameter(1, label.getVote().getId()).setParameter(2, label.getCreated())
				.setParameter(3, label.getFullLabel()).executeUpdate();
	}

	public short getRelevance(final String function) {
		try {
			return em.createQuery("SELECT l.rel  FROM Label l WHERE l.function = ?1", Short.class)
					.setParameter(1, function).getSingleResult();
		} catch (final PersistenceException ex) {
			LOGGER.error("get relevance {}: {}", function, ex.toString());
			return 1;
		}

	}

}
