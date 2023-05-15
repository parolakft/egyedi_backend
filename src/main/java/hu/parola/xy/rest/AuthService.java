// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.rest;

import static hu.parola.xy.ejb.AbstractDao.EMAIL_ALREADY_REGISTERED;
import static hu.parola.xy.ejb.AbstractDao.INVALID_TOKEN;
import static hu.parola.xy.ejb.AbstractDao.MISSING_FACEBOOK_DATA;
import static hu.parola.xy.ejb.AbstractDao.MISSING_REGISTER_DATA;
import static hu.parola.xy.ejb.AbstractDao.MISSING_RESEND_DATA;
import static hu.parola.xy.ejb.AbstractDao.NO_AUTH;
import static hu.parola.xy.ejb.AbstractDao.NO_RESEND_TOKEN;
import static hu.parola.xy.ejb.AbstractDao.USER_IS_NOT_ACTIVE;

import java.time.LocalDateTime;

import javax.ejb.EJB;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.Tools;
import hu.parola.xy.auth.JwtManager;
import hu.parola.xy.dto.ConfirmRequest;
import hu.parola.xy.dto.EmailData;
import hu.parola.xy.dto.LoggedInResponse;
import hu.parola.xy.dto.LoginRequest;
import hu.parola.xy.ejb.ClassesDao;
import hu.parola.xy.ejb.UserDao;
import hu.parola.xy.ejb.UsersClassesDao;
import hu.parola.xy.entity.Classes;
import hu.parola.xy.entity.User;
import hu.parola.xy.entity.UsersClasses;

@Path("/auth")
public class AuthService extends AbstractService {

	@EJB
	private UserDao users;
	@EJB
	private ClassesDao classes;
	@EJB
	private UsersClassesDao ucs;

	//

	public AuthService() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

	//

	private static final String LOG_RESPONSE = "response: {}";
	private static final String LOG_TOKEN = "token: {}";
	private static final String LOG_USER = "user: {}";

	private static final String[] ROLES = { "customer" };

	/**
	 * Login for the mobile app.
	 * 
	 * @return
	 */
	@POST
	@Path("/login")
	public Response login(final LoginRequest req) {
		try {
			final User user = users.login(req.getEmail(), req.getPassword());
			LOGGER.debug(LOG_USER, user);
			if (null != user) {
				if (!user.isActive()) {
					return errorResponse(USER_IS_NOT_ACTIVE);
				}
				final Object token = JwtManager.createJwt(user, ROLES);
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(user, token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
		return errorResponse(NO_AUTH);
	}

	/**
	 * Login for the admin webapp.
	 * 
	 * @return
	 */
	@POST
	@Path("/login2")
	public Response adminLogin(final LoginRequest req) {
		try {
			final User user = users.login(req.getEmail(), req.getPassword());
			LOGGER.debug(LOG_USER, user);
			if (null != user) {
				if (!user.isActive()) {
					return errorResponse(USER_IS_NOT_ACTIVE);
				}
				// Csak ADMIN, vagy FEJLESZTO léphet be...
				if (!users.hasClasses(user, Classes.ADMIN, Classes.FEJLESZTO)) {
					return errorResponse(NO_AUTH);
				}
				final Object token = JwtManager.createJwt(user, "admin");
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(user, token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
		return errorResponse(NO_AUTH);
	}

	/**
	 * Login for the központi system synchronization.
	 * 
	 * @return
	 */
	@POST
	@Path("/login3")
	public Response syncLogin(final LoginRequest req) {
		try {
			final User user = users.login(req.getEmail(), req.getPassword());
			LOGGER.debug(LOG_USER, user);
			if (null != user) {
				if (!user.isActive()) {
					return errorResponse(USER_IS_NOT_ACTIVE);
				}
				// Csak a központi rendszer szinkronizáló usere léphet be...
				if (!users.hasClasses(user, Classes.KOZPONTI)) {
					return errorResponse(NO_AUTH);
				}
				final Object token = JwtManager.createJwt(user, "synchronizer");
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(user, token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
		return errorResponse(NO_AUTH);
	}

	@POST
	@Path("/lostPassword")
	public Response lostPassword(EmailData emailData) {
		try {
			users.lostPassword(emailData.getEmail());
		} catch (Exception ex) {
			LOGGER.error("lostPassword", ex);
			return errorResponse(ex);
		}
		return Response.ok(OK).build();
	}

	@POST
	@Path("/register")
	public Response register(final LoginRequest req) {
		try {
			if (Tools.isEmptyOrNull(req.getEmail()) || Tools.isEmptyOrNull(req.getPassword())
					|| Tools.isEmptyOrNull(req.getName())) {
				return errorResponse(MISSING_REGISTER_DATA);
			}
			User user = users.findByEmail(req.getEmail());
			LOGGER.debug(LOG_USER, user);
			if (null != user) {
				return errorResponse(EMAIL_ALREADY_REGISTERED);
			} else {
				LOGGER.trace("save new user: {}", req.getEmail());
				// Új User példány létrehozása, feltöltése adatokkal
				user = new User();
				user.setPassword(Tools.encodeMd5(req.getPassword()));
				user.setEmail(req.getEmail());
				user.setName(req.getName());
				user.setCreator(req.getEmail());
				user.setCreated(LocalDateTime.now());
				user.setModifier(req.getEmail());
				user.setModified(LocalDateTime.now());
				user.generateActivator();
				// Classes példány betöltése PK alapján
				final Classes cl = classes.load(4);
				// UsersClasses példány létrehozása, feltöltése
				final UsersClasses uc = new UsersClasses();
				uc.setUser(user);
				uc.setClasses(cl);
				uc.setCreator(req.getEmail());
				uc.setCreated(LocalDateTime.now());
				uc.setModifier(req.getEmail());
				uc.setModified(LocalDateTime.now());
				// Példányok letárolása
				users.create(user);
				ucs.create(uc);
				// Levél kiküldése
				users.sendActivatorMail(user);
				return objResponse(users.load(user.getId()));
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/confirm")
	public Response confirm(final ConfirmRequest req) {

		LOGGER.info("confirm: {}", req.getToken());
		// Login
		final User user = users.confirm(req.getToken());
		if (null != user) {
			// DONE Activálás
			user.setActive(true);
			user.setToken(null);
			users.save(user);
			return Response.ok(OK).build();
		}
		return errorResponse(INVALID_TOKEN);
	}

	@POST
	@Path("/reset")
	public Response reset(final ConfirmRequest req) {

		LOGGER.info("reset: {}", req.getToken());
		// Login
		final User user = users.confirm(req.getToken());
		if (null != user) {
			// DONE új jelszó
			user.setPassword(Tools.encodeMd5(req.getPassword()));
			user.setToken(null);
			users.save(user);
			return Response.ok(OK).build();
		}
		return errorResponse(INVALID_TOKEN);
	}

	@POST
	@Path("/facebookLogin")
	public Response facebookLogin(final LoginRequest req) {
		try {
			if (Tools.isEmptyOrNull(req.getEmail()) || Tools.isEmptyOrNull(req.getFacebookId())
					|| Tools.isEmptyOrNull(req.getName())) {
				return errorResponse(MISSING_FACEBOOK_DATA);
			}
			User user = users.findByFacebookId(req.getFacebookId());
			LOGGER.debug(LOG_USER, user);
			if (null != user && user.getEmail().equals(req.getEmail()) && user.getName().equals(req.getName())) {
				// Beléptetés
				final Object token = JwtManager.createJwt(user, ROLES);
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(user, token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			} else if (null != user) {
				if (!user.getEmail().equals(req.getEmail())) {
					User userMail = users.findByEmail(req.getEmail());
					LOGGER.debug("userMail: {}", userMail);
					if (null != userMail) {
						return errorResponse(EMAIL_ALREADY_REGISTERED);
					}
				}
				user.setEmail(req.getEmail());
				user.setName(req.getName());
				user.setModifier(req.getEmail());
				user.setModified(LocalDateTime.now());
				users.save(user);
				// Beléptetés
				final Object token = JwtManager.createJwt(user, ROLES);
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(user, token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			} else {
				user = users.findByEmail(req.getEmail());
				LOGGER.debug(LOG_USER, user);
				if (null != user) {
					return errorResponse(EMAIL_ALREADY_REGISTERED);
				}
				LOGGER.trace("save new facebook user: {}, {}", req.getFacebookId(), req.getEmail());
				// Új User példány létrehozása, feltöltése adatokkal
				user = new User();
				user.setPassword(Tools.encodeMd5(req.getFacebookId() + System.currentTimeMillis() + req.getEmail()));
				user.setFacebookId(req.getFacebookId());
				user.setEmail(req.getEmail());
				user.setName(req.getName());
				user.setActive(true);
				user.setCreator(req.getEmail());
				user.setCreated(LocalDateTime.now());
				user.setModifier(req.getEmail());
				user.setModified(LocalDateTime.now());
				// Classes példány betöltése PK alapján
				final Classes cl = classes.load(4);
				// UsersClasses példány létrehozása, feltöltése
				final UsersClasses uc = new UsersClasses();
				uc.setUser(user);
				uc.setClasses(cl);
				uc.setCreator(req.getEmail());
				uc.setCreated(LocalDateTime.now());
				uc.setModifier(req.getEmail());
				uc.setModified(LocalDateTime.now());
				// Példányok letárolása
				users.create(user);
				ucs.create(uc);
				// Beléptetés
				final Object token = JwtManager.createJwt(user, ROLES);
				LOGGER.debug(LOG_TOKEN, token);
				final LoggedInResponse resp = new LoggedInResponse(users.load(user.getId()), token);
				LOGGER.debug(LOG_RESPONSE, resp);
				return Response.ok(resp).build();
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

	@POST
	@Path("/resendLink")
	public Response resendLink(final LoginRequest req) {
		try {
			if (Tools.isEmptyOrNull(req.getEmail())) {
				return errorResponse(MISSING_RESEND_DATA);
			}
			User user = users.findByEmail(req.getEmail());
			LOGGER.debug(LOG_USER, user);
			if (null == user || Tools.isEmptyOrNull(user.getToken())) {
				return errorResponse(NO_RESEND_TOKEN);
			} else {
				if (user.isActive()) {
					LOGGER.trace("resendLostPasswordMail: {}", req.getEmail());
					users.sendLostPasswordMail(user);
				} else {
					LOGGER.trace("resendActivatorMail: {}", req.getEmail());
					users.sendActivatorMail(user);
				}

				return Response.ok(OK).build();
			}
		} catch (final Exception ex) {
			return errorResponse(ex);
		}
	}

}
