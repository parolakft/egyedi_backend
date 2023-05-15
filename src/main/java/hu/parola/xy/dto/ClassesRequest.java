// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request adatszerkezet, egy felhasználók tevékenységének szűrésére
 */
@JsonInclude(Include.NON_NULL)
public class ClassesRequest extends IdRequest {

	private List<Integer> classes;

	//

	public ClassesRequest() {
		super();
	}

	//

	public List<Integer> getClasses() {
		return classes;
	}

	public void setClasses(List<Integer> classes) {
		this.classes = classes;
	}

}
