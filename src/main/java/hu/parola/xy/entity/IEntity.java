// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.entity;

import java.io.Serializable;

/**
 *
 */

public interface IEntity extends Serializable {

	Integer getId();

	static Integer getId(IEntity entity) {
		return null == entity ? null : entity.getId();
	}

}
