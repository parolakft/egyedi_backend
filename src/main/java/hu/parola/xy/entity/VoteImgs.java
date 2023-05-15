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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.Tools;
import hu.parola.xy.enums.Kind;

/**
 * Szavazatokhoz feltöltött képek
 */
@javax.persistence.Entity()
@Table(name = "vote_imgs")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "vote" }, ignoreUnknown = true)
public class VoteImgs implements hu.parola.xy.entity.IEntity {

	@Id()
	@Column(name = "vote_img_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@OneToOne
	@JoinColumn(name = "vote_id", nullable = false)
	private Vote vote;

	@Column(nullable = false)
	private String image;

	@Column(name = "vote_imgctst", nullable = false)
	private LocalDateTime created;

	//

	public VoteImgs() {
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	@Transient
	public boolean isHasImage() {
		return Tools.getTempPath(Kind.votes, id, image).exists();
	}

}
