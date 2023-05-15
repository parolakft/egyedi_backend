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
@Entity()
@Table(name = "classes")
@JsonInclude(Include.NON_NULL)
public class Classes extends AbstractEntity {

	@Id()
	@Column(name = "class_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, unique = true)
	private String name;
	@Column(nullable = false)
	private Integer type = 0;
	@Column(nullable = false)
	private int priority;
	@Column(name = "vote_count", nullable = false)
	private int voteCount = 0;

	@Column(name = "classcuser", nullable = false)
	private String creator;
	@Column(name = "classmuser", nullable = false)
	private String modifier;
	@Column(name = "classctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "classmtst", nullable = false)
	private LocalDateTime modified;

	//

	public Classes() {
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
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

	//

	public static final int FEJLESZTO = 9;
	public static final int ADMIN = 8;

	public static final int KOZPONTI = 7;
	
	public static final int VEGFELHASZNALO = 1;
	public static final int KULSO = 2;
	public static final int UZEMVEZETO = 3;
	public static final int BOLT = 4;

}
