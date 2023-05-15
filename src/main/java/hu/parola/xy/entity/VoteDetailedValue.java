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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.enums.VoteValue;

/**
 * Részletes értékelések
 */
@javax.persistence.Entity()
@Table(name = "vote_details_value")
@JsonInclude(Include.NON_NULL)
public class VoteDetailedValue implements hu.parola.xy.entity.IEntity {

	@Id()
	@Column(name = "vote_detail_value_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "vote_detail_id", nullable = false)
	private VoteDetailed detail;
	@ManyToOne
	@JoinColumn(name = "vote_id", nullable = false)
	private Vote vote;
	@Column(name = "vote_detail_value", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private VoteValue value;

	@Column(name = "vote_detail_valuectst", nullable = false)
	private LocalDateTime created;

	//

	public VoteDetailedValue() {
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

	public VoteDetailed getDetail() {
		return detail;
	}

	public void setDetail(VoteDetailed detail) {
		this.detail = detail;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public VoteValue getValue() {
		return value;
	}

	public void setValue(VoteValue value) {
		this.value = value;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

}
