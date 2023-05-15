// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.UsersClasses;

@Stateless
public class UsersClassesDao extends AbstractDao<UsersClasses> {

	@EJB
	private MailerService mailerService;

	public UsersClassesDao() {
		super(UsersClasses.class);
	}

	// private static final Logger LOGGER = LoggerFactory.getLogger(UsersClassesDao.class);

	//

}
