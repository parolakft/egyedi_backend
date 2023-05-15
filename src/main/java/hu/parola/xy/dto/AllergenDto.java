// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.entity.Allergen;

/**
 * Allergének
 */
@JsonInclude(Include.NON_NULL)
public class AllergenDto {

	private Integer id;
	private String name;
	private String icon;
	private Integer order;
	private String code;

	//

	public AllergenDto() {
		super();
	}

	public AllergenDto(final Allergen other) {
		this();
		this.id = other.getId();
		this.name = other.getName();
		this.icon = other.getIcon();
		this.order = other.getOrder();
		this.code = other.getCode();
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

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
