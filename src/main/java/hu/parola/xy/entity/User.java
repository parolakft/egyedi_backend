// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import hu.parola.xy.Tools;
import hu.parola.xy.enums.Gender;
import hu.parola.xy.enums.Kind;

/**
 * Felhasználók
 */
@Entity()
@Table(name = "users")
@JsonInclude(Include.NON_NULL)
//@JsonIgnoreProperties(value = { "password", "creator", "created", "modifier", "modified" }, ignoreUnknown = true)
public class User extends AbstractEntity implements IImageEntity {

	@Id()
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(nullable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private boolean active = false;

	@Column
	private String image;

	@Column
	private String token;
	@Column(name = "facebook_id")
	private String facebookId;
	@Column(name = "last_activity", nullable = false)
	private LocalDateTime lastActivity = LocalDateTime.now();
	@Column
	@Enumerated(EnumType.ORDINAL)
	private Gender gender = Gender.UNSPECIFIED;
	@Column
	private LocalDate birthdate;

	@Column(name = "usercuser", nullable = false)
	private String creator;
	@Column(name = "usermuser", nullable = false)
	private String modifier;
	@Column(name = "userctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "usermtst", nullable = false)
	private LocalDateTime modified;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_classes", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "class_id"))
	private Set<Classes> classes;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_allergens", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "allergen_id"))
	private Set<Allergen> allergies;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "users_groups", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
//	private Set<Group> groups = new HashSet<>();

	// Szavazatok számának mutatása: A vote > 0 akkor is FALSE-t ad vissza, ha vote IS NULL...
	@Formula("( SELECT COUNT(v.vote_id) FROM votes v WHERE v.user_id = user_id AND v.vote > 0 )")
	private Integer votes;

	//

	public User() {
		super();
	}

	private static final long serialVersionUID = 1L;

	// private static final Logger LOGGER = LoggerFactory.getLogger(User.class);

	//

	@Override
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String login) {
		this.email = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public LocalDateTime getLastActivity() {
		return lastActivity;
	}

	public void setLastActivity(LocalDateTime lastActivity) {
		this.lastActivity = lastActivity;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
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

	public Set<Classes> getClasses() {
		return classes;
	}

	public void setClasses(Set<Classes> classes) {
		this.classes = classes;
	}

	public Set<Allergen> getAllergies() {
		return allergies;
	}

	public void setAllergies(Set<Allergen> allergies) {
		this.allergies = allergies;
	}

	public void generateActivator() {
		token = Tools.encodeMd5(email + System.currentTimeMillis() + password);
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	@Transient
	public boolean isHasImage() {
		final File file = Tools.getTempPath(Kind.profiles, id, image);
		// LOGGER.warn(file.toString());
		return file.exists();
	}

}
