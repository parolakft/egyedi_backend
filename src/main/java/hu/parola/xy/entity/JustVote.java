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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.enums.VoteValue;

/**
 * Szavazatok
 */
@Entity
@Table(name = "votes")
@JsonInclude(Include.NON_NULL)
public class JustVote implements IEntity, IImageEntity {

	@Id
	@Column(name = "vote_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	@JsonIgnoreProperties({ "allergens", "nutritions", "norm", "groups", "votes" })
	private Item item;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnoreProperties({ "classes", "allergies", "votes" })
	private User user;
	@Column(name = "vote", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private VoteValue value = VoteValue.UNKNOWN;
	@Column(name = "voteftst", nullable = false)
	private LocalDateTime created;
	@Column
	private boolean active = true;

	@OneToOne(mappedBy = "vote")
	private VoteReviews review;
	@OneToOne(mappedBy = "vote")
	private VoteImgs images;
	// @OneToMany(fetch = FetchType.EAGER) @JoinColumn(name = "vote_id", referencedColumnName = "vote_id") @JsonIgnoreProperties({ "vote" })
	// private Set<VoteDetailedValue> details;

	//

	public JustVote() {
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public VoteReviews getReview() {
		return review;
	}

	public void setReview(VoteReviews review) {
		this.review = review;
	}

	public VoteImgs getImages() {
		return images;
	}

	public void setImages(VoteImgs image) {
		this.images = image;
	}

	public String getImage() {
		return null != images ? images.getImage() : null;
	}

}
