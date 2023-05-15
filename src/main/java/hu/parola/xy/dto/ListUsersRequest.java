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
public class ListUsersRequest extends ListRequest {

	private String email;

	//

	public ListUsersRequest() {
		super();
	}

	//

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
