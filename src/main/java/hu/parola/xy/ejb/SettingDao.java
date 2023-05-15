// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.ejb;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.MailerService;
import hu.parola.xy.entity.Settings;
import hu.parola.xy.entity.User;
import hu.parola.xy.synchron.SynchronizeParman;

@Stateless
public class SettingDao extends AbstractDao<Settings> {

	@EJB
	private MailerService mailerService;

	@Resource
	private ManagedExecutorService executorService;

	//

	public SettingDao() {
		super(Settings.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingDao.class);

	//

	public List<Settings> list(String... names) {
		return list(Arrays.asList(names));
	}

	public List<Settings> list(List<String> names) {
		return em.createQuery("SELECT s FROM Settings s WHERE s.name IN ?1", Settings.class).setParameter(1, names)
				.getResultList();
	}

	public void synchronize() {
		// elindítjuk a háttérfolyamatot.
		final SynchronizeParman task = new SynchronizeParman();
		executorService.submit(task);
	}

	//

	public Settings get(final String name) {
		try {
			return em.createQuery("SELECT s FROM Settings s WHERE s.name = ?1", Settings.class).setParameter(1, name)
					.getSingleResult();
		} catch (PersistenceException ex) {
			LOGGER.error("Get {}: {}", name, ex.toString());
			return null;
		}
	}

	public LocalDateTime getDateTime(final String name) {
		Settings cfg = null;
		try {
			cfg = get(name);
			if (null != cfg && null != cfg.getValue() && !cfg.getValue().isEmpty())
				return LocalDateTime.parse(cfg.getValue());
		} catch (final Exception ex) {
			LOGGER.error("Parse {}: {}", cfg.getValue(), ex);
		}
		return null;
	}

	public Integer getInteger(final String name) {
		Settings cfg = null;
		try {
			cfg = get(name);
			if (null != cfg && null != cfg.getValue() && !cfg.getValue().isEmpty())
				return Integer.valueOf(cfg.getValue());
		} catch (final Exception ex) {
			LOGGER.error("Parse {}: {}", cfg.getValue(), ex);
		}
		return null;
	}

	public Double getDouble(final String name) {
		Settings cfg = null;
		try {
			cfg = get(name);
			if (null != cfg && null != cfg.getValue() && !cfg.getValue().isEmpty())
				return Double.valueOf(cfg.getValue());
		} catch (final Exception ex) {
			LOGGER.error("Parse {}: {}", cfg.getValue(), ex);
		}
		return null;
	}

	public Long getLong(final String name) {
		Settings cfg = null;
		try {
			cfg = get(name);
			if (null != cfg && null != cfg.getValue() && !cfg.getValue().isEmpty())
				return Long.valueOf(cfg.getValue());
		} catch (final Exception ex) {
			LOGGER.error("Parse {}: {}", cfg.getValue(), ex);
		}
		return null;
	}

	//

	public void set(final String name, final String value, final User user) {
		set(name, value, user.getEmail());
	}

	public void set(final String name, final String value, final String user) {
		Settings cfg = get(name);
		if (null == cfg) {
			cfg = new Settings();
			cfg.setName(name);
		}
		cfg.init(user);
		cfg.setValue(value);
		save(cfg);
	}

	// XY-1014 Logikai törlés
	public void delete(Integer id, User musr) {
		final Settings cfg = load(id);
		cfg.setDeleted(!cfg.isDeleted());
		cfg.init(musr);
		save(cfg);
	}

}
