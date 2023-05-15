// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import hu.parola.xy.enums.VoteValue;

/**
 *
 */
public class VoteRequest extends IdRequest {

	private Integer detail;
	private VoteValue value;

	//

	public VoteRequest() {
		super();
	}

	//

	public VoteValue getValue() {
		return value;
	}

	public void setValue(VoteValue value) {
		this.value = value;
	}

	public Integer getDetail() {
		return detail;
	}

	public void setDetail(Integer detail) {
		this.detail = detail;
	}

}
