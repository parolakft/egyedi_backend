// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.entity.NutritionsDetails;

/**
 * Tápértékek
 */
@JsonInclude(Include.NON_NULL)
public class NutritionsDetailsDto {

	private Integer id;
	private String name;
	private String icon;
	private String fullName;
	private String details;

	//

	public NutritionsDetailsDto() {
		super();
	}

	public NutritionsDetailsDto(final NutritionsDetails other) {
		this();
		this.id = other.getId();
		this.name = other.getName();
		this.icon = other.getIcon();
		this.fullName = other.getFullName();
		this.details = other.getDetails();
	}

	//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
