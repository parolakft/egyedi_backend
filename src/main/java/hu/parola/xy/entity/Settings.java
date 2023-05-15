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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 *
 */
@Entity
@Table(name = "settings")
@JsonInclude(Include.NON_NULL)
public class Settings extends AbstractEntity {

	@Id()
	@Column(name = "setting_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, unique = true)
	private String name;
	@Column(nullable = false)
	private String value;
	// XY-1014 Logikai törlés
	@Column
	private boolean deleted;

	@Column(name = "settingcuser", nullable = false)
	private String creator;
	@Column(name = "settingmuser", nullable = false)
	private String modifier;
	@Column(name = "settingctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "settingmtst", nullable = false)
	private LocalDateTime modified;

	//

	public Settings() {
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

	// XY-1014 Logikai törlés
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
