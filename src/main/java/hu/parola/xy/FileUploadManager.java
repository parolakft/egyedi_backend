// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME Ezt majd JCA kompatibilisre kell csinálni
public final class FileUploadManager {

	private FileUploadManager() {
		super();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadManager.class);

	//

	public static Integer getId(MultipartFormDataInput input) {
		try {
			return Integer.parseInt(getField("id", input));
		} catch (NumberFormatException ex) {
			LOGGER.error("getId", ex);
		}
		return null;
	}

	private static String getField(String key, MultipartFormDataInput input) {
		try {
			final List<InputPart> fields = input.getFormDataMap().get(key);
			if (!Tools.isEmptyOrNull(fields))
				return fields.get(0).getBodyAsString();
		} catch (IOException | NumberFormatException ex) {
			LOGGER.error("getField", ex);
		}
		return null;
	}

	/**
	 * header sample { Content-Type=[image/png], Content-Disposition=[form-data;
	 * name="file"; filename="filename.extension"] }
	 **/
	// get uploaded filename, is there a easy way in RESTEasy?
	private static String getFileName(MultivaluedMap<String, String> header) {
		final String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
		for (final String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {
				final String[] files = filename.split("=");
				final String oldFileName = files[1].trim().replace("\"", "").replace(" ", "_").replace("(", "")
						.replace(")", "");
				String[] tokens = oldFileName.split("\\.(?=[^.]+$)");
				return tokens[0] + '-' + DateTimeFormatter.ofPattern("yyyyMMddHHmmA").format(LocalDateTime.now()) + '.'
						+ tokens[1];
			}
		}
		return "unknown";
	}

	public static String fileUpload(MultipartFormDataInput input, File dir) {
		String newFileName = null;
		List<InputPart> files = input.getFormDataMap().get("file");
		if (files != null) {
			InputPart file = files.get(0);
			try {
				// convert the uploaded file to inputstream
				InputStream inputStream = file.getBody(InputStream.class, null);
				// constructs upload file path
				newFileName = getFileName(file.getHeaders());
				final File newFile = new File(dir, newFileName);
				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				try (FileOutputStream fop = new FileOutputStream(newFile)) {
					inputStream.transferTo(fop);
					fop.flush();
				}
				LOGGER.info("File {} feltöltve", newFileName);
			} catch (IOException ex) {
				LOGGER.error("fileName", ex);
			}
		}
		return newFileName;
	}

}
