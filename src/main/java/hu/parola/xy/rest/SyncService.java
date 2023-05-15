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

import hu.parola.xy.dto.VoteRequest;
import hu.parola.xy.ejb.VoteDao;

/**
 * Központi rendszer felő történő szinkronizálás.
 */
@Path("/synchron")
public class SyncService extends AbstractService {

	@EJB
	private VoteDao votes;

	//

	public SyncService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncService.class);

	//

	@POST
	@Path("/votes")
	public Response vote(VoteRequest req) {
		try {
			LOGGER.trace("synchronize votes");
			return listResponse(votes.sync());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
