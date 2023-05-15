// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import static hu.parola.xy.ejb.AbstractDao.INVALID_ID;
import static hu.parola.xy.ejb.AbstractDao.NO_AUTH;
import static hu.parola.xy.ejb.AbstractDao.PASSWORDS_OLD;

import java.io.File;
import java.time.LocalDate;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.FileUploadManager;
import hu.parola.xy.Tools;
import hu.parola.xy.auth.JwtManager;
import hu.parola.xy.dto.AllergiesRequest;
import hu.parola.xy.dto.DetailResponse;
import hu.parola.xy.dto.IdRequest;
import hu.parola.xy.dto.ListIdRequest;
import hu.parola.xy.dto.LoggedInResponse;
import hu.parola.xy.dto.LoginRequest;
import hu.parola.xy.dto.ObjRequest;
import hu.parola.xy.dto.ReviewRequest;
import hu.parola.xy.dto.VoteRequest;
import hu.parola.xy.ejb.AllergenDao;
import hu.parola.xy.ejb.CategoryDao;
import hu.parola.xy.ejb.ItemDao;
import hu.parola.xy.ejb.SettingDao;
import hu.parola.xy.ejb.VoteDao;
import hu.parola.xy.entity.User;
import hu.parola.xy.enums.Gender;
import hu.parola.xy.enums.Kind;

/**
 *
 */
@Path("/mobile")
public class MobileService extends AbstractService {

	@EJB
	private CategoryDao cats;
	@EJB
	private AllergenDao alls;
	@EJB
	private ItemDao items;
	@EJB
	private VoteDao votes;
	@EJB
	private SettingDao cfg;

	//

	public MobileService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(MobileService.class);

	//

	@POST
	@Path("/getUser")
	public Response getUserData() {
		try {
			LOGGER.trace("get user data");
			return objResponse(getUser());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/allergens")
	public Response getAllergens() {
		try {
			LOGGER.trace("get all allergens");
			return listResponse(alls.listAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	//

	@POST
	@Path("/setAllergies")
	public Response setAllergies(AllergiesRequest req) {
		try {
			LOGGER.trace("set allergies {}", req.getIds());
			// DONE Tényleges adatfeldolgozás...
			users.setAllergies(getUserId(), req.getIds());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/setBirthDate")
	public Response setBirthDate(ObjRequest<LocalDate> req) {
		try {
			LOGGER.trace("set birth date {}", req.getData());
			final User user = getUser();
			user.setBirthdate(req.getData());
			user.init(user);
			users.save(user);
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/setGenderData")
	public Response setGenderData(ObjRequest<Gender> req) {
		try {
			LOGGER.trace("set gender {}", req.getData());
			final User user = getUser();
			user.setGender(req.getData());
			user.init(user);
			users.save(user);
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	//

	@POST
	@Path("/mainCategories")
	public Response getMainCats() {
		try {
			LOGGER.trace("get main categories list");
			return listResponse(cats.findAll());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/items")
	public Response getItems(final ListIdRequest req) {
		try {
			LOGGER.trace("get category {}'s list of items from {} for {}", req.getId(), req.getOffset(),
					req.getLimit());
			return listResponse(items.list(req));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/itemDetails")
	public Response getItem(IdRequest req) {
		try {
			LOGGER.trace("get item with all details {}", req.getId());
			return Response.ok(
					new DetailResponse(items.loadAll(req.getId()), cfg.list("leírás", "összetevők", "tápérték", "kép")))
					.build();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	//

	@POST
	@Path("/getInfo")
	public Response getInfo() {
		try {
			LOGGER.trace("get info (névjegy)");
			return objResponse(cfg.get("nevjegy").getValue());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/getPrivacy")
	public Response getPrivacy() {
		try {
			LOGGER.trace("get privacy info");
			return objResponse(cfg.get("adatvedelem").getValue());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	//

	@POST
	@Path("/see")
	public Response see(IdRequest req) {
		try {
			LOGGER.trace("see an item {}", req.getId());
			return objResponse(votes.createEmpty(getUser(), req.getId()));
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/vote")
	public Response vote(VoteRequest req) {
		try {
			LOGGER.trace("vote on a item {} {}", req.getId(), req.getValue());
			votes.cast(req.getId(), req.getValue());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/changePassword")
	public Response changePassword(LoginRequest req) {
		try {
			if (Tools.isEmptyOrNull(req.getPassword()) || Tools.isEmptyOrNull(req.getNewPassword())) {
				return errorResponse("MISSING_CHANGEPASSWORD_DATA");
			}
			// Bejelentkezett felhasználó
			final User user = getUser();
			LOGGER.trace("changePassword userId:{}", user.getId());

			// Jelszavak MD5-be
			final String oldPw = Tools.encodeMd5(req.getPassword());
			final String newPw = Tools.encodeMd5(req.getNewPassword());
			if (!oldPw.equals(user.getPassword()))
				return errorResponse(NO_AUTH);
			// Jelszavak ellenőrzései
			if (newPw.equals(oldPw))
				return errorResponse(PASSWORDS_OLD);
			// A lényeg: Jelszóváltoztatás
			user.setPassword(newPw);
			user.init(user);
			users.save(user);
			// Új token generálás
			final Object token = JwtManager.createJwt(user, "customer");
			LOGGER.debug("token: {}", token);
			final LoggedInResponse resp = new LoggedInResponse(user, token);
			LOGGER.debug("response: {}", resp);
			return Response.ok(resp).build();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/voteDetails")
	public Response getVoteDetails() {
		try {
			LOGGER.trace("vote details");
			return listResponse(votes.listDetaileds());
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/voteDetailed")
	public Response voteDeatiled(VoteRequest req) {
		try {
			LOGGER.trace("detailed vote on a item {} {}", req.getId(), req.getValue());
			votes.castDetailed(req.getId(), req.getDetail(), req.getValue());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/voteReview")
	public Response setVoteReview(ReviewRequest req) {
		try {
			LOGGER.trace("vote review {}", req.getId());
			votes.review(req.getId(), req.getText());
			return okResponse();
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/voteImage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadVoteImage(MultipartFormDataInput req) {
		try {
			final Integer id = FileUploadManager.getId(req);
			LOGGER.trace("upload vote image {}", id);
			if (!Tools.isValidId(id))
				throw new IllegalArgumentException(INVALID_ID);
			// XY-1015 Képkezelés
			final File dir = Tools.getTempUpload(Kind.votes);
			final String tmp = FileUploadManager.fileUpload(req, dir);
			votes.setImage(id, new File(dir, tmp));
			return okResponse();
		} catch (final Exception ex) {
			LOGGER.error("image upload", ex);
			return errorResponse(ex);
		}
	}

}
