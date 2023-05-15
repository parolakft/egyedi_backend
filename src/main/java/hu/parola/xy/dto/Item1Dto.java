// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.ejb.AbstractDao;
import hu.parola.xy.entity.Item;

/**
 * Cikkek
 */
@JsonInclude(Include.NON_NULL)
public class Item1Dto {

	private Integer id;
	private String name;
	private String shortName;
	private String description;
	private String image;
	private float rating = 0;
	private String ingredients;
	private String subtitle;
	private List<ItemsAllergenDto> allergens;
	private List<NutritionDto> nutritions;
	private Integer categoryId;
	private Integer votes;

	//

	public Item1Dto() {
		super();
	}

	public Item1Dto(final Item item) {
		this();
		if (null == item)
			throw new NullPointerException(AbstractDao.NOT_FOUND);
		this.id = item.getId();
		this.name = item.getName();
		this.shortName = item.getShortName();
		this.description = item.getDescription();
		this.image = item.getImage();
		this.rating = item.getRating();
		this.ingredients = item.getIngredients();
		this.subtitle = item.getSubtitle();
		this.categoryId = item.getCategoryId();
		this.votes = item.getVotes();
		//
		this.allergens = item.getAllergens().stream().map(ItemsAllergenDto::new).collect(Collectors.toList());
		//
		this.nutritions = item.getNutritions().stream().map(NutritionDto::new).collect(Collectors.toList());
	}

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
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

	public List<ItemsAllergenDto> getAllergens() {
		return allergens;
	}

	public void setAllergens(List<ItemsAllergenDto> allergens) {
		this.allergens = allergens;
	}

	public List<NutritionDto> getNutritions() {
		return nutritions;
	}

	public void setNutritions(List<NutritionDto> nutritions) {
		this.nutritions = nutritions;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getVotes() {
		return votes;
	}

	public void setVotes(Integer votes) {
		this.votes = votes;
	}

}
