// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------


package hu.parola.xy.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * @see https://stackoverflow.com/a/28803634/4899193
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

	private final ObjectMapper MAPPER;

	public ObjectMapperContextResolver() {

		MAPPER = new ObjectMapper();
		// Now you should use JavaTimeModule instead JSR310Module...
		MAPPER.registerModule(new JavaTimeModule());
		MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	@Override
	public ObjectMapper getContext(final Class<?> type) {

		return MAPPER;
	}

}
