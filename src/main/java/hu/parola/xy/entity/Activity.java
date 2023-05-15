// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.Tools;
import hu.parola.xy.enums.Kind;
import hu.parola.xy.enums.VoteValue;

@Entity
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "itemImage", "userImage" })
public class Activity {

	@Id
	@Column(name = "vote_id")
	private Integer id;
	@Column(name = "item_id")
	private Integer itemId;
	@Column(name = "item_name")
	private String itemName;
	@Column(name = "item_image")
	private String itemImage;
	@Column(name = "user_id")
	private Integer userId;
	@Column(name = "user_name")
	private String userName;
	@Column(name = "user_image")
	private String userImage;
	private LocalDateTime created;
	@Column(name = "vote")
	private VoteValue value;
	private String review;
	private String image;
	@Transient
	private Map<String, VoteValue> details = new HashMap<>();

	//

	public Activity() {
		super();
	}

	//

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public VoteValue getValue() {
		return value;
	}

	public void setValue(VoteValue value) {
		this.value = value;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Map<String, VoteValue> getDetails() {
		return details;
	}

	public void setDetails(Map<String, VoteValue> details) {
		this.details = details;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String images) {
		this.image = images;
	}

	//

	@Transient
	public boolean isHasImage() {
		return Tools.getTempPath(Kind.votes, id, image).exists();
	}

	@Transient
	public boolean isItemHasImage() {
		return Tools.getTempPath(Kind.items, itemId, itemImage).exists();
	}

	@Transient
	public boolean isUserHasImage() {
		return Tools.getTempPath(Kind.profiles, userId, userImage).exists();
	}

}
