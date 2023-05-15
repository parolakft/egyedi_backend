// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class MiscDao {

	@PersistenceContext
	protected EntityManager em;

	//

	public MiscDao() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MiscDao.class);

	//

	public <T> T load(Class<? extends T> clazz, Integer id) {
		LOGGER.debug("load {}: {}", clazz.getSimpleName(), id);
		return em.find(clazz, id);
	}

}
