// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.auth;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.text.ParseException;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSASigner;

import hu.parola.xy.entity.User;
import net.minidev.json.JSONObject;

/**
 * <p>
 * JWT-t kezelő osztály
 * </p>
 * <p>
 * Induláskor egyszer betölti a beállításokat, és a kulcsot, aláíráskor már
 * memóriából használja őket.
 * </p>
 */
@ApplicationScoped
public class JwtManager {

	private static final PrivateKey privateKey;

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtManager.class);

	//

	static {
		FileInputStream fis = null;
		PrivateKey pk = null;
		try {
			final KeyStore ks = KeyStore.getInstance("JKS");
			final String configDir = System.getProperty("jboss.server.config.dir");
			final String alias = System.getProperty("keystore.aliasname", "alias");
			final char[] password = System.getProperty("keystore.password", "secret").toCharArray();
			final File keystorePath = new File(configDir, "jwt.keystore");
			LOGGER.info("Keyfile: {}", keystorePath);
			fis = new FileInputStream(keystorePath);
			ks.load(fis, password);
			final Key key = ks.getKey(alias, password);
			LOGGER.debug("key: {}", null == key ? null : key.getClass());
			if (key instanceof PrivateKey) {
				pk = (PrivateKey) key;
			}
		} catch (final Exception e) {
			LOGGER.error("Loading private key: ERROR", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final Exception e) {
					LOGGER.error("Close private key file: ERROR", e);
				}
			}
		}
		privateKey = pk;
	}

	//

	/** Érvényesség: 24 óra */
	private static final int TOKEN_VALIDITY = 60 * 60 * 24;
	private static final String CLAIM_ROLES = "groups";
	private static final String ISSUER = "xy-jwt-issuer";
	private static final String AUDIENCE = "jwt-audience";

	/**
	 * JWT létrehozása
	 * 
	 * @param user  Bejelentkezett felhasználó
	 * @param roles Szerepek
	 * @return JWT token szöveges formában
	 */
	public static String createJwt(User user, final String... roles) {

		if (user != null) {
			try {
				final JWSSigner signer = new RSASSASigner(privateKey);
				final JsonArrayBuilder rolesBuilder = Json.createArrayBuilder();
				for (final String role : roles) {
					rolesBuilder.add(role);
				}
				final var rolesArray = rolesBuilder.build();
				final JsonObjectBuilder claimsBuilder = Json.createObjectBuilder()
						// SUBscriber: előfizető = user
						.add("sub", user.getEmail())
						// ISSuer: kiállító
						.add("iss", ISSUER)
						// AUDience: felhasználók
						.add("aud", AUDIENCE)
						// GROUPS: szerepkörök
						.add(CLAIM_ROLES, rolesArray)
						// EXPire: lejárat
						.add("exp", System.currentTimeMillis() / 1000 + TOKEN_VALIDITY)
						// USER ID
						.add("user", user.getId());
				final String payload = claimsBuilder.build().toString();
				final JWSObject jwsObject = new JWSObject(
						new JWSHeader.Builder(JWSAlgorithm.RS256).type(new JOSEObjectType("jwt")).build(),
						new Payload(payload));
				jwsObject.sign(signer);
				return jwsObject.serialize();
			} catch (final Exception ex) {
				LOGGER.error("createJwt", ex);
				throw new IllegalArgumentException("TECHNICAL");
			}
		} else {
			throw new IllegalArgumentException("EMPTY");
		}
	}

	/**
	 * JWT visszakódolása
	 * 
	 * @param token A JWT token, mint szöveg
	 * @return A JWT "payload" mind JSON objektum
	 * @throws ParseException
	 */
	public static JSONObject parseJwt(final String token) throws ParseException {

		final JWSObject jws = JWSObject.parse(token);
		final JSONObject payload = jws.getPayload().toJSONObject();
		LOGGER.trace("payload = {}", payload);
		return payload;
	}

}
