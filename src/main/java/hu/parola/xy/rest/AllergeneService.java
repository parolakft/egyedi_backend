// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import java.time.LocalDateTime;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.ejb.AllergenDao;
import hu.parola.xy.entity.Allergen;
import hu.parola.xy.entity.User;

@Path("/protected/allergens")
public class AllergeneService extends AbstractService {

	@EJB
	private AllergenDao alls;

	//

	public AllergeneService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AllergeneService.class);

	//

	@POST
	@Path("/all")
	public Response listAll() {
		try {
			LOGGER.trace("list allergens");
			return listResponse(alls.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get allergen: {}", req.getId());
			return objResponse(alls.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final Allergen req) {
		try {
			final User user = getUser();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.trace("save new allergen");
				req.init(user);
				alls.create(req);
				return objResponse(req);
			} else {
				LOGGER.trace("edit allergen: {}", id);
				final Allergen item = alls.load(id);
				item.setName(req.getName());
				item.setCode(req.getCode());
				item.setOrder(req.getOrder());
				item.setIcon(req.getIcon());
				item.setModified(LocalDateTime.now());
				item.setModifier(user.getEmail());
				return objResponse(alls.save(item));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete allergen: {}", req.getId());
			alls.remove(req.getId());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
