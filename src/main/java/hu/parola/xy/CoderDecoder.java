// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CoderDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoderDecoder.class);

	//

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
	static {
		JSON_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		// Dátum-/időkezelés
		// Now you should use JavaTimeModule instead
		JSON_MAPPER.registerModule(new JavaTimeModule());
		JSON_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		JSON_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		// FIXME Kollekciók
		// try { final SimpleModule module = new SimpleModule("CollectionLogSerializer",
		// new Version(1, 0, 0, null, null, null)); module.addSerializer(
		// new CollectionLogSerializer()); JSON_MAPPER.registerModule(module); } catch
		// (final Exception ex) { LOGGER.error("Cannot register module",
		// ex); }
	}

	//

	public static String toJSON(final Object src) {

		String res = null;
		try {
			if (null != src) {
				res = JSON_MAPPER.writeValueAsString(src);
			}
		} catch (final JsonProcessingException ex) {
			LOGGER.error(ex.toString());
			res = "{\"error\":\"" + ex.getMessage() + "\"}";
		}
		return res;
	}

	public static <T> T fromJSON(final String json, final Class<T> klass) {

		T res = null;
		try {
			if (null != json) {
				res = JSON_MAPPER.readValue(json, klass);
			}
		} catch (final IOException ex) {
			LOGGER.error(ex.toString());
			res = null;
		}
		return res;
	}

}
