// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.Tools;
import hu.parola.xy.ejb.MiscDao;
import hu.parola.xy.entity.Category;
import hu.parola.xy.entity.IImageEntity;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.User;
import hu.parola.xy.entity.Vote;
import hu.parola.xy.enums.Kind;

@Path("/images")
public class ImageService extends AbstractService {

	@EJB
	private MiscDao dao;

	//

	public ImageService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

	//

	private static final Map<Kind, Class<? extends IImageEntity>> MAP = Map.of(Kind.items, Item.class, Kind.cats,
			Category.class, Kind.profiles, User.class, Kind.votes, Vote.class);

	@GET
	@Path("/{kind}/{id}")
	public Response get(@PathParam("kind") final Kind kind, @PathParam("id") final Integer id) {
		try {
			final IImageEntity item = dao.load(MAP.get(kind), id);
			final File path = Tools.getTempPath(kind, id, item.getImage());
			LOGGER.trace("get {} image: {} {}", kind, id, path);
			// Cannot close the stream, as the JAX-RS framework needs it open...
			final FileInputStream in = new FileInputStream(path);
			// Disable cacheing...
			final CacheControl cc = new CacheControl();
			cc.setMaxAge(0);
			cc.setMustRevalidate(true);
			cc.setNoCache(true);
			cc.setNoStore(true);
			final String type = URLConnection.guessContentTypeFromName(path.getName());
			return Response.ok().cacheControl(cc).entity(in).type(type).build();
		} catch (final Exception ex) {
			return errorHtmlResponse(ex);
		}
	}

}
