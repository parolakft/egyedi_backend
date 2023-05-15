// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Közös response adatszerkezet, leszármaztatáshoz is.
 */
@JsonInclude(Include.NON_NULL)
public class CommonResponse implements Serializable {

	private String status;

	private List<String> messages;

	// Konstansok

	/** Státuszkód: Siker */
	public static final String OK = "OK";
	/** Státuszkód: Hiba */
	public static final String ERROR = "ERROR";
	/** Nem futást jelentó státuszkódok */
	public static final String[] NOT_RUNNING = { OK, ERROR };

	/** Konstruktor: OK */
	public CommonResponse() {
		status = OK;
		messages = null;
	}

	/**
	 * Konstruktor: 1 HIBA
	 * <p>
	 * vagy semmi, ha az NULL...
	 * </p>
	 * 
	 * @param message Hibaüzenet
	 */
	public CommonResponse(final String message) {
		if (null == message) {
			messages = new ArrayList<>();
		} else {
			messages = new ArrayList<>(1);
			addMessage(message);
		}
	}

	/**
	 * Konstruktor: TÖBB HIBA
	 * 
	 * @param messages Hibaüzenetek
	 */
	public CommonResponse(final String... messages) {
		this.messages = new ArrayList<>(messages.length);
		addMessages(messages);
	}

	/**
	 * Konstruktor: TÖBB HIBA
	 * 
	 * @param messages Hibaüzenetek
	 */
	public CommonResponse(final Collection<String> messages) {
		this.messages = new ArrayList<>(messages.size());
		addMessages(messages);
	}

	private static final long serialVersionUID = 1L;

	//

	public final String getStatus() {
		return status;
	}

	public final void setStatus(final String status) {
		this.status = status;
	}

	public final List<String> getMessages() {
		return messages;
	}

	/**
	 * 1 HIBA beállítása
	 * 
	 * @param message Hibaüzenet
	 */
	public final void addMessage(final String message) {
		status = ERROR;
		if (messages == null) {
			messages = new ArrayList<>();
		}
		messages.add(message);
	}

	/**
	 * TÖBB HIBA beállítása
	 * 
	 * @param messages Hibaüzenetek
	 */
	public final void addMessages(final String... messages) {
		status = ERROR;
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		Collections.addAll(this.messages, messages);
	}

	/**
	 * TÖBB HIBA beállítása
	 * 
	 * @param messages Hibaüzenetek
	 */
	public final void addMessages(final Collection<String> messages) {
		status = ERROR;
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		this.messages.addAll(messages);
	}

}
