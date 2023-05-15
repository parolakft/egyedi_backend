// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.Classes;

@Stateless
public class ClassesDao extends AbstractDao<Classes> {

	@EJB
	private MailerService mailerService;

	public ClassesDao() {
		super(Classes.class);
	}

	// private static final Logger LOGGER = LoggerFactory.getLogger(ClassesDao.class);

	//

}
