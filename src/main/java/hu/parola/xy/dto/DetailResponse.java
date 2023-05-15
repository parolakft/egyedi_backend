// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import hu.parola.xy.entity.Settings;

/**
 * Response adatszerkezet, itemDetails operációhoz
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailResponse extends ObjectResponse<Item1Dto> {

	private List<Settings> cfg;

	//

	public DetailResponse() {
		super();
	}

	public DetailResponse(Item1Dto item, List<Settings> list) {
		this();
		setData(item);
		cfg = list;
	}

	private static final long serialVersionUID = 1L;

	//

	public List<Settings> getCfg() {
		return cfg;
	}

	public void setCfg(List<Settings> cfg) {
		this.cfg = cfg;
	}

}
