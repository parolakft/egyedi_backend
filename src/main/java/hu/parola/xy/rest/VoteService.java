// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.dto.ActivitiesRequest;
import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.ejb.VoteDao;
import hu.parola.xy.entity.Classes;

@Path("/protected/votes")
public class VoteService extends AbstractService {

	@EJB
	private VoteDao votes;

	//

	public VoteService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VoteService.class);

	//

	@POST
	@Path("/list")
	public Response list(final ActivitiesRequest req) {
		try {
			LOGGER.trace("list votes {}", req);
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			return listResponse(votes.list(req));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get user: {}", req.getId());
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			return objResponse(users.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
