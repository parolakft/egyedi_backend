// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import static hu.parola.xy.ejb.AbstractDao.INVALID_ID;

import java.io.File;
import java.time.LocalDateTime;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.FileUploadManager;
import hu.parola.xy.Tools;
import hu.parola.xy.dto.ActivitiesRequest;
import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.dto.ListItemsRequest;
import hu.parola.xy.ejb.CategoryDao;
import hu.parola.xy.ejb.ItemDao;
import hu.parola.xy.ejb.VoteDao;
import hu.parola.xy.entity.Classes;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.ActKind;
import hu.parola.xy.enums.Kind;

@Path("/protected/items")
public class ItemService extends AbstractService {

	@EJB
	private ItemDao items;
	@EJB
	private CategoryDao cats;
	@EJB
	private VoteDao votes;

	//

	public ItemService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

	//

	@POST
	@Path("/list")
	public Response listAll(final ListItemsRequest req) {
		try {
			LOGGER.trace("list items");
			return listResponse(items.list(req));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get item: {}", req.getId());
			return objResponse(items.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final Item req) {
		try {
			final User user = getUser();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.trace("save new item");
				req.init(user);
				items.create(req);
				return objResponse(req);
			} else {
				LOGGER.trace("edit item: {}", id);
				final Item item = items.load(id);

				item.setName(req.getName());
				item.setShortName(req.getShortName());
				item.setSubtitle(req.getSubtitle());
				item.setDescription(req.getDescription());
				item.setIngredients(req.getIngredients());
				item.setCode(req.getCode());
				item.setParman(req.getParman());
				item.setRating(req.getRating());
				item.setModifier(user.getEmail());
				item.setModified(LocalDateTime.now());
				item.setCategory(Tools.isValidId(req.getCategoryId()) ? cats.load(req.getCategoryId()) : null);
				return objResponse(items.save(item));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete item: {}", req.getId());
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			// XY-1014 Logikai törlés
			items.delete(req.getId(), getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/uploadImage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadImage(MultipartFormDataInput req) {
		try {
			final Integer id = FileUploadManager.getId(req);
			LOGGER.trace("upload image for item {}", id);
			if (!Tools.isValidId(id))
				throw new IllegalArgumentException(INVALID_ID);
			// XY-1015 Képkezelés
			final File dir = Tools.getTempUpload(Kind.items);
			final String tmp = FileUploadManager.fileUpload(req, dir);
			LOGGER.debug("pre setImage {}, {}", id, tmp);
			items.setImage(id, getUser(), new File(dir, tmp));
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/deleteImage")
	public Response deleteImage(IdRequest req) {
		try {
			final Integer id = req.getId();
			LOGGER.trace("upload image for item {}", id);
			if (!Tools.isValidId(id))
				throw new IllegalArgumentException(INVALID_ID);
			items.removeImage(id, getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/votes")
	public Response listVotes(final ActivitiesRequest req) {
		try {
			LOGGER.trace("list item's votes");
			req.setKind(ActKind.item);
			return listResponse(votes.list(req));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
