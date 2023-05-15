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
import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.dto.ParentRequest;
import hu.parola.xy.ejb.CategoryDao;
import hu.parola.xy.entity.Category;
import hu.parola.xy.entity.Classes;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.Kind;

@Path("/protected/categories")
public class CategoryService extends AbstractService {

	@EJB
	private CategoryDao cats;

	//

	public CategoryService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);

	//

	@POST
	@Path("/all")
	public Response listAll() {
		try {
			LOGGER.trace("list categories");
			return listResponse(cats.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/get")
	public Response get(final IdRequest req) {
		try {
			LOGGER.trace("get category: {}", req.getId());
			return objResponse(cats.load(req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/save")
	public Response save(final Category req) {
		try {
			final User user = getUser();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.trace("save new category: {}", id);
				final Integer pid = req.getParentId();
				req.init(user);

				cats.create(req);
				cats.setParent(req.getId(), pid);
				return objResponse(req);
			} else {
				LOGGER.trace("edit category: {}", id);
				final Category cat = cats.load(id);
				cat.setName(req.getName());
				cat.setModified(LocalDateTime.now());
				cat.setModifier(user.getEmail());
				cats.setParent(id, req.getParentId());
				return objResponse(cats.save(cat));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/delete")
	public Response delete(final IdRequest req) {
		try {
			LOGGER.trace("delete user: {}", req.getId());
			if (!users.hasClasses(getUser(), Classes.ADMIN, Classes.FEJLESZTO))
				return Response.status(Status.FORBIDDEN).build();
			cats.delete(req.getId(), getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/parent")
	public Response setParent(final ParentRequest req) {
		try {
			LOGGER.trace("set category's {} parent to {}", req.getId(), req.getParent());
			cats.setParent(req.getId(), req.getParent());
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
			final File dir = Tools.getTempUpload(Kind.cats);
			final String tmp = FileUploadManager.fileUpload(req, dir);
			cats.setImage(id, getUser(), new File(dir, tmp));
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
			cats.removeImage(id, getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
