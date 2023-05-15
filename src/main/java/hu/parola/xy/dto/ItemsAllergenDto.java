// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.entity.ItemsAllergen;
import hu.parola.xy.enums.AllergenValue;

/**
 * Cikk allergénjei
 */
@JsonInclude(Include.NON_NULL)
public class ItemsAllergenDto {

	private Integer id;
	private AllergenDto allergen;
	private AllergenValue info;

	//

	public ItemsAllergenDto() {
		super();
	}

	public ItemsAllergenDto(final ItemsAllergen other) {
		this();
		this.id = other.getId();
		this.allergen = new AllergenDto(other.getAllergen());
		this.info = other.getInfo();
	}

	//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AllergenDto getAllergen() {
		return allergen;
	}

	public void setAllergen(AllergenDto allergen) {
		this.allergen = allergen;
	}

	public AllergenValue getInfo() {
		return info;
	}

	public void setInfo(AllergenValue info) {
		this.info = info;
	}

}
