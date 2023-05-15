// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HttpPostClient {

	protected HttpPostClient() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpPostClient.class);

	//

	/// A HTTP féle POST metódust végrehajtó metódus
	protected String post(String address, String body) throws IOException {
		final HttpURLConnection con = (HttpURLConnection) new URL(address).openConnection();
		// DONE Beállítások küldés előtt
		con.setRequestMethod("POST");
		// Body
		con.setDoOutput(true);
		try (final OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8)) {
			os.append(body);
		}
		// Kapcsolódás (küldés)
		con.connect();
		LOGGER.trace("Post: {} {}", con.getResponseMessage(), con.getResponseCode());
		// Eredmény
		if (con.getResponseCode() == 200) {
			// Siker
			return Tools.load(con.getInputStream());
		}
		// Hiba esetén
		throw new IOException(getResponse(con));
	}

	// Kilogolja/Visszaadja a HTTP státuszt "kód - üzenet" formában.
	// Kilogolja a kapott HTML hibaoldalt is...
	protected String getResponse(HttpURLConnection con) throws IOException {
		final String msg = String.format("%d - %s", con.getResponseCode(), con.getResponseMessage());
		LOGGER.error("POST: {}", msg);
		if (LOGGER.isTraceEnabled())
			LOGGER.trace(Tools.load(con.getErrorStream()));
		return msg;
	}

}
