// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response adatszerkezet, objektum visszaadásához
 * 
 * @param <T> Az objektum típusa
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectResponse<T> extends CommonResponse {

	private T data;

	//

	public ObjectResponse() {
	}

	public ObjectResponse(final T item) {
		this.data = item;
	}

	private static final long serialVersionUID = 1L;

	//

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
