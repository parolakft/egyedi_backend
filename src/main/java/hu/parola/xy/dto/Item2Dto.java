// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.ejb.AbstractDao;
import hu.parola.xy.entity.Item;
import hu.parola.xy.entity.ItemsAllergen;

/**
 * Cikkek
 */
@JsonInclude(Include.NON_NULL)
public class Item2Dto {

	private Integer id;
	private String name;
	private String shortName;
	private String image;
	private List<ItemsAllergen> allergens;
	private Integer categoryId;
	private Integer votes;

	//

	public Item2Dto() {
		super();
	}

	public Item2Dto(final Item item) {
		this();
		if (null == item)
			throw new NullPointerException(AbstractDao.NOT_FOUND);
		this.id = item.getId();
		this.name = item.getName();
		this.shortName = item.getShortName();
		this.image = item.getImage();
		this.allergens = item.getAllergens();
		this.categoryId = item.getCategoryId();
		this.votes = item.getVotes();
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<ItemsAllergen> getAllergens() {
		return allergens;
	}

	public void setAllergens(List<ItemsAllergen> allergens) {
		this.allergens = allergens;
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
