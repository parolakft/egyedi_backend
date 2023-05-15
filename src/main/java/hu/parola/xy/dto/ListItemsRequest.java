// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request adatszerkezet, egy felhasználók szűrésére
 */
@JsonInclude(Include.NON_NULL)
public class ListItemsRequest extends ListRequest {

	private List<Integer> cats;

	//

	public ListItemsRequest() {
		super();
	}

	//

	public List<Integer> getCats() {
		return cats;
	}

	public void setCats(List<Integer> cats) {
		this.cats = cats;
	}

}
