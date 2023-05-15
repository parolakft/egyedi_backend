// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hu.parola.xy.Tools;
import hu.parola.xy.enums.Kind;

/**
 * Kategóriák
 */
@Entity()
@Table(name = "categories")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value = { "parent", "creator", "created", "modifier", "modified" }, ignoreUnknown = true)
public class Category extends AbstractEntity implements IImageEntity {

	@Id()
	@Column(name = "category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(nullable = false)
	private String name;
	// XY-1015 Képkezelés
	@Column
	private String image;
	// XY-1014 Logikai törlés
	@Column
	private boolean deleted;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	@Column(name = "categorycuser", nullable = false)
	private String creator;
	@Column(name = "categorymuser", nullable = false)
	private String modifier;
	@Column(name = "categoryctst", nullable = false)
	private LocalDateTime created;
	@Column(name = "categorymtst", nullable = false)
	private LocalDateTime modified;

	@Transient
	private Integer parentId;
	@Transient
	private List<Category> children;
	@Transient
	private List<Item> items;

	//

	@Formula("(SELECT COUNT(i.item_id) FROM items i WHERE i.category_id = category_id)")
	private Integer itemCount;
	@Formula("(SELECT COUNT(i.item_id) FROM items i WHERE i.category_id = category_id AND NOT i.deleted)")
	private Integer activeItemCount;

	@Formula("(SELECT COUNT(c.category_id) FROM categories c WHERE c.parent_id = category_id)")
	private Integer childCount;

	//

	public Category() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// XY-1015 Képkezelés
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
		parentId = IEntity.getId(parent);
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

	public Integer getParentId() {
		return null != parentId ? parentId : IEntity.getId(parent);
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public List<Category> getChildren() {
		return children;
	}

	public void setChildren(List<Category> children) {
		this.children = children;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	@Transient
	public boolean isHasImage() {
		// XY-1015 Képkezelés
		return Tools.getTempPath(Kind.cats, id, image).exists();
	}

	public Integer getItemCount() {
		return itemCount;
	}

	public Integer getActiveItemCount() {
		return activeItemCount;
	}

	public Integer getChildCount() {
		return childCount;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
