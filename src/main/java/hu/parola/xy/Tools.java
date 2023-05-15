// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.enums.Kind;

public final class Tools {

	//

	private Tools() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

	//

	public static boolean isEmptyOrNull(final String str) {
		return null == str || str.isEmpty();
	}

	public static boolean isEmptyOrNull(final Collection<?> coll) {
		return null == coll || coll.isEmpty();
	}

	public static <T> boolean isEmptyOrNull(final T[] array) {
		return null == array || 0 == array.length;
	}

	public static String coalesce(final String a, final String b) {

		return isEmptyOrNull(a) ? b : a;
	}

	@SafeVarargs
	public static String coalesce(final String... items) {

		if (null != items) {
			for (final String item : items)
				if (!isEmptyOrNull(item))
					return item;
		}
		return null;
	}

	public static <T extends Object> T coalesce(final T a, final T b) {

		return null == a ? b : a;
	}

	/**
	 * @param items
	 * @return
	 */
	@SafeVarargs
	public static <T> T coalesce(final T... items) {

		if (null != items) {
			for (final T item : items)
				if (null != item)
					return item;
		}
		return null;
	}

	public static <T> T ifNull(final T a, final T b) {

		return null == a ? b : a;
	}

	public static String ifEmpty(final String a, final String b) {

		return isEmptyOrNull(a) ? b : a;
	}

	public static <T> T nullIf(final T a, final T b) {

		return Objects.equals(a, b) ? null : a;
	}

	//

	@SafeVarargs
	public static <T> boolean in(final T item, final T... array) {

		if (null == item) {
			for (final T member : array)
				if (null == member)
					return true;
		} else {
			for (final T member : array)
				if (item.equals(member))
					return true;
		}
		return false;
	}
	//

	public static boolean between(final LocalDate date, final LocalDate from, final LocalDate to) {

		if (null == date)
			throw new IllegalArgumentException();
		return !(null == from || null == to || from.isAfter(date) || to.isBefore(date));
	}

	//

	public static String encodeMd5(final String string) {

		// HEX
		return String.format("%032x", hashMd5(string));
	}

	public static BigInteger hashMd5(final String pass) {

		// DONE encode password
		BigInteger result = BigInteger.ZERO;
		try {
			// MD5
			final MessageDigest md5 = MessageDigest.getInstance("MD5");
			final byte[] hash = md5.digest(pass.getBytes(StandardCharsets.UTF_8));
			result = new BigInteger(1, hash);
		} catch (final Exception e) {
			LOGGER.error("MD5", e);
		}
		return result;
	}

	public static String removePasswordFromJsonString(final String jsonString) {

		if (jsonString == null)
			return null;
		// Van "oldPassword", "newPassword", stb mező is.
		return jsonString.replaceAll("(p|P)assword\"\\s*:\\s*\"[^\"]*\"", "$1assword\":\"[FILTERED]\"");
	}

	public static void sleep(final long l) {

		sleep(l, false);

	}

	public static void sleep(final long l, final boolean logging) {

		try {
			if (logging) {
				LOGGER.info("SLEEP: Sleeping {}ms", l);
			}
			Thread.sleep(l);
		} catch (final InterruptedException e) {
			LOGGER.error(e.getLocalizedMessage());
		}
		if (logging) {
			LOGGER.info("SLEEP: Just woke up...");
		}
	}

	//

	public static boolean isValidId(final Integer key) {

		return null != key && key != 0;
	}

	/**
	 * UTF-8 kódolású szöveges erőforrás beolvasása Stream-ből egy {@code String}-be.
	 *
	 * @param stream .
	 * @return A beolvasott szöveg, hiba esetén {@code NULL}.
	 */
	public static String load(final InputStream stream) {

		final StringBuilder result = new StringBuilder(1024);
		try {
			final InputStreamReader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
			try {
				int l = 0;
				final char[] buffer = new char[1024];
				do {
					l = in.read(buffer, 0, 1024);
					if (l > 0) {
						result.append(buffer, 0, l);
					}
				} while (l >= 0);

			} finally {
				in.close();
			}
		} catch (final Exception e) {
			LOGGER.error(e.toString());
		}
		return result.toString();
	}

	//

	public static final String WEBROOT_PROPERTY = "file.webroot.dir";
	public static final String WEBROOT_DEFAULT = "/home/xy/webroot";
	public static final String DYNAMIC_FOLDER = "/dynamic/";

	public static File getTemp(String property, String defaults, String subFolder, Kind kind, String file) {
		return new File(System.getProperty(property, defaults) + subFolder + kind, file);
	}

	public static File getTempUpload(Kind kind) {
		return getTemp(WEBROOT_PROPERTY, WEBROOT_DEFAULT, DYNAMIC_FOLDER, kind, "").getParentFile();
	}

	public static File getTempPath(Kind kind, Integer id, String ext) {
		return getTemp(WEBROOT_PROPERTY, WEBROOT_DEFAULT, DYNAMIC_FOLDER, kind, "/" + id + ext);
	}

}
