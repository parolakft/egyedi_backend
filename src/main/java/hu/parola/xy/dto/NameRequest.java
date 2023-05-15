// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request adatszerkezet, egy ID mezővel
 */
@JsonInclude(Include.NON_NULL)
public class NameRequest {

	private String name;

	//

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
