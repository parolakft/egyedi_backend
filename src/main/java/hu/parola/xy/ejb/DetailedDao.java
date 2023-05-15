// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.VoteDetailed;

@Stateless
public class DetailedDao extends AbstractDao<VoteDetailed> {

	@EJB
	private MailerService mailerService;

	public DetailedDao() {
		super(VoteDetailed.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DetailedDao.class);

	//

	@Override
	public List<VoteDetailed> listAll() {
		return em.createQuery("SELECT d FROM VoteDetailed d ORDER BY d.order NULLS LAST", entityClass).getResultList();
	}

	public void setOrder(final Integer id, final Integer order) {
		final String msg = (String) em.createNativeQuery("SELECT * FROM set_order_vote_details(?1, ?2)")
				.setParameter(1, order).setParameter(2, id).getSingleResult();
		LOGGER.info("setOrder: {}", msg);
	}

}
