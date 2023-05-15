// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import hu.parola.xy.Tools;

/**
 *
 */
// A @JsonIgnoreProperties -t egyes leszármazottak felülírják, ezért oda is be kell tenni ezeket a propertyket...
@JsonIgnoreProperties(value = { "creator", "created", "modifier", "modified" }, ignoreUnknown = true)
public abstract class AbstractEntity implements IEntity {

	public abstract String getCreator();

	public abstract void setCreator(String usr);

	public abstract String getModifier();

	public abstract void setModifier(String usr);

	public abstract LocalDateTime getCreated();

	public abstract void setCreated(LocalDateTime time);

	public abstract LocalDateTime getModified();

	public abstract void setModified(LocalDateTime time);

	//

	protected AbstractEntity() {
		super();
	}

	private static final long serialVersionUID = 1L;

	//

	@Override
	public boolean equals(final Object obj) {
		return null != obj && obj.getClass().equals(getClass()) && Objects.equals(((IEntity) obj).getId(), getId());
	}

	@Override
	public int hashCode() {

		return Objects.hash(getId());
	}

	//

	public void init(final User usr) {
		init(usr.getEmail());
	}

	public void init(final String usr) {
		setModifier(usr);
		setModified(LocalDateTime.now());
		if (Tools.isEmptyOrNull(getCreator()))
			setCreator(usr);
		if (null == getCreated())
			setCreated(getModified());
	}

}
