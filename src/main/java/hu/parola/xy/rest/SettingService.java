// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import java.time.LocalDateTime;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.resource.spi.IllegalStateException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.dto.NameRequest;
import hu.parola.xy.ejb.ItemDao;
import hu.parola.xy.ejb.SettingDao;
import hu.parola.xy.entity.Settings;
import hu.parola.xy.entity.User;
import hu.parola.xy.label.LabellerTask;
import hu.parola.xy.synchron.SynchronizeParmanTask;

@Path("/protected/settings")
public class SettingService extends AbstractService {

	@EJB
	private SettingDao cfgs;
	@EJB
	private ItemDao items;
	@EJB
	private SynchronizeParmanTask sync;
	@EJB
	private LabellerTask labe;
	@Resource
	private ManagedExecutorService es;

	//

	public SettingService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SettingService.class);

	//

	@POST
	@Path("/all")
	public Response listAll() {
		try {
			LOGGER.trace("list settings");
			return listResponse(cfgs.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final NameRequest req) {
		try {
			LOGGER.trace("get settings: {}", req.getName());
			return objResponse(cfgs.get(req.getName()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final Settings req) {
		try {
			final User user = getUser();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.trace("save new setting");
				req.init(user);
				cfgs.create(req);
				return objResponse(req);
			} else {
				LOGGER.trace("edit setting: {}", id);
				final Settings item = cfgs.load(id);
				item.setName(req.getName());
				item.setValue(req.getValue());
				item.setModified(LocalDateTime.now());
				item.setModifier(user.getEmail());
				return objResponse(cfgs.save(item));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete setting: {}", req.getId());
			// XY-1014 Logikai törlés
			cfgs.delete(req.getId(), getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	// Szinkronizálás a parMAN rendszer felől

	@POST
	@Path("/synchronize")
	public Response synchronize() {
		try {
			LOGGER.info("Starting synchronization...");
			if (sync.isRunning()) {
				LOGGER.error("Synchronization is already running!");
				throw new IllegalStateException("Synchronization is already running!");
			}
			es.submit(sync);
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/syncStatus")
	public Response getSyncStatus() {
		try {
			LOGGER.info("Getting status of synchronization...");
			return objResponse(sync.getLog());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	// Szemantikus címkéző futtatása

	@POST
	@Path("/labels")
	public Response labels() {
		try {
			LOGGER.info("Starting labeller...");
			if (labe.isRunning()) {
				LOGGER.error("Labeller is already running!");
				throw new IllegalStateException("Labeller is already running!");
			}
			es.submit(labe);
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/labelStatus")
	public Response getLabelStatus() {
		try {
			LOGGER.info("Getting status of labeller...");
			return objResponse(labe.getLog());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
