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
public class ListIdRequest extends ListRequest {

	private Integer offset = 0;
	private Integer id;

	//

	public ListIdRequest() {
		super();
		limit = 20;
	}

	//

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
