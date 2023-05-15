// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 */
// NULL mellet az üres tömb se jelenik meg....
@JsonInclude(Include.NON_EMPTY)
public class CoolResponse implements Serializable {

	private List<Integer> coolIds;
	private String error;

	//

	public CoolResponse() {
		super();
	}

	private static final long serialVersionUID = 1L;

	//

	public List<Integer> getCoolIds() {
		if (null == coolIds)
			coolIds = new ArrayList<>();
		return coolIds;
	}

	public void setCoolIds(List<Integer> coolIds) {
		this.coolIds = coolIds;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
