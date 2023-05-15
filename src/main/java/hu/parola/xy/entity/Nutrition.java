// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Cikk tápértékei
 */
@javax.persistence.Entity()
@Table(name = "nutritions")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "item", "creator", "created", "modifier", "modified" })
public class Nutrition extends AbstractEntity {

	@Id()
	@Column(name = "nutrition_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	@ManyToOne
	@JoinColumn(name = "nutrition_detail_id", nullable = false)
	private NutritionsDetails detail;
	@Column(nullable = false)
	private String value;

	@Column(name = "nutritioncuser", nullable = false)
	private String creator;
	@Column(name = "nutritionmuser", nullable = false)
	private String modifier;
	@Column(name = "nutritionctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "nutritionmtst", nullable = false)
	private LocalDateTime modified;

	//

	public Nutrition() {
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

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public NutritionsDetails getDetail() {
		return detail;
	}

	public void setDetail(NutritionsDetails detail) {
		this.detail = detail;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

}
