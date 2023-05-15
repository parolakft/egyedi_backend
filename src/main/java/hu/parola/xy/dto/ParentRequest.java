// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

/**
 *
 */
public class ParentRequest extends IdRequest {

	private Integer parent;

	//

	public ParentRequest() {
		super();
	}

	//

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

}
