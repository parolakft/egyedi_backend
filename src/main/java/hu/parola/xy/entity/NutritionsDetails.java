// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Tápértékek
 */
@javax.persistence.Entity()
@Table(name = "nutritions_details")
@JsonInclude(Include.NON_NULL)
public class NutritionsDetails implements hu.parola.xy.entity.IEntity {

	@Id()
	@Column(name = "nutrition_detail_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "nutrition_name", nullable = false)
	private String name;
	@Column(nullable = false)
	private String icon;
	@Column(name = "nutrition_full_name", nullable = false)
	private String fullName;
	@Column(name = "nutrition_details")
	private String details;

	//

	public NutritionsDetails() {
		super();
	}

	private static final long serialVersionUID = 1L;

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
