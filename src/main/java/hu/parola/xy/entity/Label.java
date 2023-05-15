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
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Címkék definíciói
 */
@javax.persistence.Entity()
@Table(name = "labels")
@JsonInclude(Include.NON_NULL)
public class Label extends AbstractEntity {

	@Id()
	@Column(name = "label_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "label_xx", nullable = false)
	private short rel = 1;
	@Column(name = "label_name", nullable = false)
	private String name;
	@Column(name = "label_function_name", nullable = false)
	private String function;

	@Column(name = "labelcuser", nullable = false)
	private String creator;
	@Column(name = "labelmuser", nullable = false)
	private String modifier;
	@Column(name = "labelctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "labelmtst", nullable = false)
	private LocalDateTime modified;

	//

	public Label() {
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

	public short getRel() {
		return rel;
	}

	public void setRel(short rel) {
		this.rel = rel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
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
