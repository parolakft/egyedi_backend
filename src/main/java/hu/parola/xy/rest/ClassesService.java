// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.ejb.ClassesDao;
import hu.parola.xy.entity.Classes;

@Path("/protected/classes")
public class ClassesService extends AbstractService {

	@EJB
	private ClassesDao clses;

	//

	public ClassesService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassesService.class);

	//

	@POST
	@Path("/all")
	public Response listAll() {
		try {
			LOGGER.trace("list classes");
			return listResponse(clses.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get class: {}", req.getId());
			return objResponse(clses.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final Classes req) {
		try {
			if (null == req.getId()) {
				LOGGER.trace("save new class: {}", req.getId());
				clses.create(req);
				return objResponse(req);
			} else {
				LOGGER.trace("edit class: {}", req.getId());
				req.init(getUser());
				return objResponse(clses.save(req));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete class: {}", req.getId());
			clses.remove(req.getId());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
