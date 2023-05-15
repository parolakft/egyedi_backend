// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.enums.VoteValue;

/**
 * Részletes értékelések kritériumai
 */
@javax.persistence.Entity()
@Table(name = "vote_details")
@JsonInclude(Include.NON_EMPTY)
public class VoteDetailed extends AbstractEntity {

	@Id()
	@Column(name = "vote_detail_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String detail;
	@Column
	private boolean active = true;
	@Column(name = "default_value", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private VoteValue defaultValue = VoteValue.MID;
	@Column(name = "ord", nullable = false)
	private Integer order;

	@Column(name = "vote_detailcuser", nullable = false)
	private String creator;
	@Column(name = "vote_detailmuser", nullable = false)
	private String modifier;
	@Column(name = "vote_detailctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "vote_detailmtst", nullable = false)
	private LocalDateTime modified;

	//

	public VoteDetailed() {
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

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public VoteValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(VoteValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
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
