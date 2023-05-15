// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request adatszerkezet, egy felhasználók szűrésére
 */
@JsonInclude(Include.NON_NULL)
public class ListVotesRequest extends ListRequest {

	private String mode;

	//

	public ListVotesRequest() {
		super();
	}

	//

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
