// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.Allergen;

@Stateless
public class AllergenDao extends AbstractDao<Allergen> {

	@EJB
	private MailerService mailerService;

	public AllergenDao() {
		super(Allergen.class);
	}

	// private static final Logger LOGGER = LoggerFactory.getLogger(AllergenDao.class);

	//

	@Override
	public List<Allergen> listAll() {
		return em.createQuery("SELECT a FROM Allergen a ORDER BY a.order", Allergen.class).getResultList();
	}

}
