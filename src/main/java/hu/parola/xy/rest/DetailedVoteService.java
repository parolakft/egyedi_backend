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
import hu.parola.xy.dto.OrderRequest;
import hu.parola.xy.ejb.DetailedDao;
import hu.parola.xy.entity.User;
import hu.parola.xy.entity.VoteDetailed;

@Path("/protected/detailed")
public class DetailedVoteService extends AbstractService {

	@EJB
	private DetailedDao votes;

	//

	public DetailedVoteService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DetailedVoteService.class);

	//

	@POST
	@Path("/all")
	public Response listAll() {
		try {
			LOGGER.trace("list detaileds");
			return listResponse(votes.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get detailed: {}", req.getId());
			return objResponse(votes.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final VoteDetailed req) {
		try {
			final User user = getUser();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.trace("save new detailed: {}", req.getId());
				req.init(user);
				votes.create(req);
				return objResponse(req);
			} else {
				LOGGER.trace("edit detailed: {}", req.getId());
				final VoteDetailed item = votes.load(id);
				item.setDetail(req.getDetail());
				item.setActive(req.isActive());
				item.setDefaultValue(req.getDefaultValue());
				item.setOrder(req.getOrder());
				item.setModified(LocalDateTime.now());
				item.setModifier(user.getEmail());
				
				return objResponse(votes.save(item));
								
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete detailed: {}", req.getId());
			votes.remove(req.getId());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	
	@POST
	@Path("/setOrder")
	public Response setOrder(final OrderRequest req) {
		try {
			LOGGER.trace("set detailed order: {}", req.getId());
			votes.setOrder(req.getId(), req.getOrder());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}
	
	
	
}
