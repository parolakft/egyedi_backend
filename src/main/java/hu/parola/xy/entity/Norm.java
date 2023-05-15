// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * Cikk anyagnormái
 */
@Entity()
@Table(name = "material_norm")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "item", "creator", "created", "modifier", "modified" })
public class Norm extends AbstractEntity {

	@Id()
	@Column(name = "material_norm_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "parman_id", nullable = false)
	private Integer parman;
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	@Column(nullable = false)
	private String value;
	@Column(name = "xy_id")
	private Integer xyId;

	@Column(name = "normcuser", nullable = false)
	private String creator;
	@Column(name = "normmuser", nullable = false)
	private String modifier;
	@Column(name = "normctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "normmtst", nullable = false)
	private LocalDateTime modified;

	//

	public Norm() {
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

	public Integer getParman() {
		return parman;
	}

	public void setParman(Integer parman) {
		this.parman = parman;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getXyId() {
		return xyId;
	}

	public void setXyId(Integer xyId) {
		this.xyId = xyId;
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
