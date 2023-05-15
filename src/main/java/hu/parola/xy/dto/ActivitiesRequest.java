// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import hu.parola.xy.CoderDecoder;
import hu.parola.xy.enums.ActKind;

/**
 * Request adatszerkezet, egy felhasználók tevékenységének szűrésére
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivitiesRequest extends IdRequest {

	private LocalDateTime to = LocalDateTime.now();
	private LocalDateTime from = to.minusMonths(2);
	@JsonProperty("mode")
	private ActKind kind;
	private Integer limit = 50;

	//

	public ActivitiesRequest() {
		super();
	}

	//

	public LocalDateTime getFrom() {
		return from;
	}

	public void setFrom(LocalDateTime from) {
		this.from = from;
	}

	public LocalDateTime getTo() {
		return to;
	}

	public void setTo(LocalDateTime to) {
		this.to = to;
	}

	public ActKind getKind() {
		return kind;
	}

	public void setKind(ActKind kind) {
		this.kind = kind;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return super.toString() + CoderDecoder.toJSON(this);
	}
	
}
