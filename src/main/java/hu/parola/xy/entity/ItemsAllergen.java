// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.enums.AllergenValue;

/**
 * Cikk allergénjei
 */
@Entity()
@Table(name = "items_allergens")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "item", "creator", "created", "modifier", "modified"  })
public class ItemsAllergen extends AbstractEntity {

	@Id()
	@Column(name = "item_allergen_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	@ManyToOne
	@JoinColumn(name = "allergen_id", nullable = false)
	private Allergen allergen;
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private AllergenValue info;

	@Column(name = "item_allergencuser", nullable = false)
	private String creator;
	@Column(name = "item_allergenmuser", nullable = false)
	private String modifier;
	@Column(name = "item_allergenctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "item_allergenmtst", nullable = false)
	private LocalDateTime modified;

	//

	public ItemsAllergen() {
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

	public Allergen getAllergen() {
		return allergen;
	}

	public void setAllergen(Allergen allergen) {
		this.allergen = allergen;
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

	public AllergenValue getInfo() {
		return info;
	}

	public void setInfo(AllergenValue info) {
		this.info = info;
	}

}
