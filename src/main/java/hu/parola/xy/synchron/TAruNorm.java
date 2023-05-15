// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class TAruNorm {

	private Integer parManId;
	private String parManKod;
	@JsonProperty("XYId")
	private Integer xyId;
	@JsonProperty("tomegSzaz")
	private String value;

	//

	public TAruNorm() {
		super();
	}

	//

	public Integer getParManId() {
		return parManId;
	}

	public void setParManId(Integer parManId) {
		this.parManId = parManId;
	}

	public String getParManKod() {
		return parManKod;
	}

	public void setParManKod(String parManKod) {
		this.parManKod = parManKod;
	}

	public Integer getXyId() {
		return xyId;
	}

	public void setXyId(Integer xyId) {
		this.xyId = xyId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
