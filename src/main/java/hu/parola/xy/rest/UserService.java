// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import static hu.parola.xy.ejb.AbstractDao.INVALID_ID;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
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
import hu.parola.xy.dto.ClassesRequest;
import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.dto.ListUsersRequest;
import hu.parola.xy.dto.LoginRequest;
import hu.parola.xy.ejb.VoteDao;
import hu.parola.xy.entity.Classes;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.ActKind;
import hu.parola.xy.enums.Kind;

@Path("/protected/users")
public class UserService extends AbstractService {

	@EJB
	private VoteDao votes;

	@Inject
	private MobileService mobil;

	//

	public UserService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	//

	@POST
	@Path("/changePassword")
	public Response changePassword(LoginRequest req) {
		return mobil.changePassword(req);
	}

	@POST
	@Path("/list")
	public Response list(final ListUsersRequest req) {
		try {
			LOGGER.trace("list users");
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			return listResponse(users.list(req));
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

	@POST
	@Path("/save")
	public Response save(final User req) {
		try {
			final User muser = getUser();
			if (!users.hasClasses(muser, Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			final Integer id = req.getId();
			if (null == id) {
				LOGGER.warn("save new user: {}", req.getEmail());
				final List<Integer> lista = req.getClasses().stream().map(Classes::getId).collect(Collectors.toList());
				req.init(muser);
				req.setPassword(Tools.encodeMd5(req.getPassword()));
				req.setClasses(null);
				final User u = users.save(req);
				users.setClasses(muser, u.getId(), lista);
				return objResponse(u);
			} else {
				LOGGER.trace("edit user: {}", req.getEmail());
				final User u = users.load(id);
				u.init(muser);
				u.setEmail(req.getEmail());
				u.setName(req.getName());
				u.setActive(req.isActive());
				u.setGender(req.getGender());
				u.setBirthdate(req.getBirthdate());
				// A jelszó csak akkor változtatandó, ha nem üres...
				if (!Tools.isEmptyOrNull(req.getPassword()))
					u.setPassword(Tools.encodeMd5(req.getPassword()));
				users.save(u);
				users.setClasses(muser, id, req.getClasses().stream().map(Classes::getId).collect(Collectors.toList()));
				return objResponse(users.load(id));
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
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			// XY-1014 Logikai törlés
			users.delete(req.getId(), getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/classes")
	public Response setClasses(final ClassesRequest req) {
		try {
			LOGGER.trace("set user's classes {} {}", req.getId(), req.getClasses());
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			users.setClasses(getUser(), req.getId(), req.getClasses());
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
			final File dir = Tools.getTempUpload(Kind.profiles);
			final String tmp = FileUploadManager.fileUpload(req, dir);
			users.setImage(id, getUser(), new File(dir, tmp));
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
			users.removeImage(id, getUser());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/activities")
	public Response listActivities(final ActivitiesRequest req) {
		try {
			LOGGER.trace("list user's activities");
			if (!users.hasClasses(getUser(), Classes.ADMIN))
				return Response.status(Status.FORBIDDEN).build();
			req.setKind(ActKind.user);
			return listResponse(votes.list(req));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
