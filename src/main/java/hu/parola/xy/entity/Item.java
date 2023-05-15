// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.Tools;
import hu.parola.xy.enums.Kind;

/**
 * Cikkek
 */
@Entity()
@Table(name = "items")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "category", "creator", "created", "modifier", "modified",
		"norm" }, ignoreUnknown = true)
public class Item extends AbstractEntity implements IImageEntity {

	@Id()
	@Column(name = "item_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "parman_id")
	private Integer parman;
	@Column(nullable = false)
	private String name;
	@Column(name = "short_name")
	private String shortName;
	@Column
	private String description;
	@Column
	private String image;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(nullable = false)
	private float rating = 0;
	@Column
	private String ingredients;
	@Column(name = "item_subtitle")
	private String subtitle;
	@Column(nullable = false)
	private boolean deleted = false;
	@Column(name = "item_code")
	private String code;

	@Column(name = "itemcuser", nullable = false)
	private String creator;
	@Column(name = "itemmuser", nullable = false)
	private String modifier;
	@Column(name = "itemctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "itemmtst", nullable = false)
	private LocalDateTime modified;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id")
	@OrderBy
	private List<ItemsAllergen> allergens;

	@OneToMany
	@JoinColumn(name = "item_id")
	@OrderBy
	private List<Nutrition> nutritions;

	@OneToMany
	@JoinColumn(name = "item_id")
	private List<Norm> norm;

	@ManyToMany
	@JoinTable(name = "items_groups", joinColumns = @JoinColumn(name = "item_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
	private List<Group> groups;

	@Transient
	private Integer categoryId;

	// Szavazatok számának mutatása. vote > 0: Ez NULL esetén is FALSE-t ad...
	@Formula("( SELECT COUNT(v.vote_id) FROM votes v WHERE v.item_id = item_id AND v.vote > 0 )")
	private Integer votes;

	//

	public Item() {
		super();
	}

	public Item(final Integer id) {
		super();
		this.id = id;
	}

	private static final long serialVersionUID = 1L;

	//

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getParman() {
		return parman;
	}

	public void setParman(Integer parman) {
		this.parman = parman;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
		categoryId = null == category ? null : category.getId();
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public List<ItemsAllergen> getAllergens() {
		return allergens;
	}

	public void setAllergens(List<ItemsAllergen> allergens) {
		this.allergens = allergens;
	}

	public List<Nutrition> getNutritions() {
		return nutritions;
	}

	public void setNutritions(List<Nutrition> nutritions) {
		this.nutritions = nutritions;
	}

	public Integer getCategoryId() {
		if (null != categoryId)
			return categoryId;
		if (null != category)
			return category.getId();
		return null;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public List<Norm> getNorm() {
		return norm;
	}

	public void setNorm(List<Norm> norm) {
		this.norm = norm;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

	@Transient
	public boolean isHasImage() {
		return Tools.getTempPath(Kind.items, id, image).exists();
	}

	@Override
	public void init(String usr) {
		super.init(usr);
		if (null == name)
			name = "";
	}

}
