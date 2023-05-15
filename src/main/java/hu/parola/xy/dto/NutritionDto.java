// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.entity.Nutrition;

/**
 * Cikk tápértékei
 */
@JsonInclude(Include.NON_NULL)
public class NutritionDto {

	private Integer id;
	private NutritionsDetailsDto detail;
	private String value;

	//

	public NutritionDto() {
		super();
	}

	public NutritionDto(final Nutrition other) {
		this();
		this.id = other.getId();
		this.detail = new NutritionsDetailsDto(other.getDetail());
		this.value = other.getValue();
	}

	//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NutritionsDetailsDto getDetail() {
		return detail;
	}

	public void setDetail(NutritionsDetailsDto detail) {
		this.detail = detail;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
