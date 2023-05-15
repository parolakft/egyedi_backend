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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Cikk allergénjei
 */
@Entity()
@Table(name = "votes_labels")
@JsonInclude(Include.NON_NULL)
public class VotesLabel implements hu.parola.xy.entity.IEntity {

	@Id()
	@Column(name = "vote_label_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "vote_id", nullable = false)
	private Vote vote;
	@Column(name = "full_label")
	private String fullLabel;
	@Column(name = "vote_labelctst", nullable = false)
	private LocalDateTime created;

	//

	public VotesLabel() {
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

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public String getFullLabel() {
		return fullLabel;
	}

	public void setFullLabel(String fullLabel) {
		this.fullLabel = fullLabel;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

}
