// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.Log;
import hu.parola.xy.Tools;
import hu.parola.xy.auth.JwtManager;
import hu.parola.xy.dto.CommonResponse;
import hu.parola.xy.dto.ListResponse;
import hu.parola.xy.dto.ObjectResponse;
import hu.parola.xy.ejb.UserDao;
import hu.parola.xy.entity.User;
import net.minidev.json.JSONObject;

/**
 *
 */
@Log
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public abstract class AbstractService {

	@Context
	private HttpHeaders headers;

	@EJB
	protected UserDao users;

	//

	protected static final CommonResponse OK = new CommonResponse();

	//

	protected AbstractService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractService.class);

	//

	protected <T> Response listResponse(final List<T> list) {
		return Response.ok(new ListResponse<T>(list)).build();
	}

	protected <T> Response objResponse(final T obj) {
		return Response.ok(new ObjectResponse<T>(obj)).build();
	}

	protected Response okResponse() {
		return Response.ok(OK).build();
	}

	protected Response errorHtmlResponse(final Exception ex) {
		// Az EJB hívások maguktól logolják az exceptionöket, nem kell még egyszer...
		Throwable e = ex;
		while (e.getCause() instanceof Exception)
			e = e.getCause();
		return Response.status(Status.NOT_FOUND).type(MediaType.TEXT_PLAIN).entity(e.toString()).build();
	}

	protected Response errorResponse(final Exception ex) {
		Throwable e = ex;
		while (e.getCause() instanceof Exception)
			e = e.getCause();
		LOGGER.error(e.toString());
		return errorResponse(e.getMessage());
	}

	protected Response errorResponse(final String msg) {
		return Response.ok(new CommonResponse(msg)).build();
	}

	//

	private static final String AUTH = "Authorization";

	private JSONObject getPayload() {
		try {
			String header = null;
			final List<String> head = headers.getRequestHeader(AUTH);
			if (!Tools.isEmptyOrNull(head))
				header = head.get(0);

			if (null != header && header.startsWith("Bearer ")) {
				return JwtManager.parseJwt(header.substring(7));
			} else {
				throw new IllegalArgumentException("Nincs JWT");
			}
		} catch (final Exception ex) {
			LOGGER.error("getPayload", ex);
			throw new IllegalArgumentException(ex.toString());
		}
	}

	protected Integer getUserId() {
		try {
			final JSONObject payload = getPayload();
			return payload.getAsNumber("user").intValue();
		} catch (final Exception ex) {
			LOGGER.error("getUserId", ex);
			throw new IllegalArgumentException(ex.toString());
		}
	}

	protected User getUser() {
		return users.load(getUserId());
	}

}
