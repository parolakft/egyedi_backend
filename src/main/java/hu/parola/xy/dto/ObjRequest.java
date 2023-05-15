// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Request adatszerkezet, egy adatmezővel
 */
@JsonInclude(Include.NON_NULL)
public class ObjRequest<T> {

	private T data;

	//

	public ObjRequest() {
		super();
	}

	//

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
