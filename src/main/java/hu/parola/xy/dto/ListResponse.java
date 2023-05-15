// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Rwesponse adatszerkezet, objektumok listájának visszaadásához
 * 
 * @param <T> A lista objektumainak típusa
 */
@JsonInclude(Include.NON_NULL)
public class ListResponse<T> extends CommonResponse {

	private List<T> list;

	//

	public ListResponse() {

		super();
		list = new ArrayList<>();
	}

	public ListResponse(final T item) {

		super();
		list = new ArrayList<>(1);
		list.add(item);
	}

	@SafeVarargs
	public ListResponse(final T... items) {

		super();
		list = new ArrayList<>(items.length);
		addItems(items);
	}

	public ListResponse(final Collection<T> items) {

		super();
		if (null != items) {
			list = new ArrayList<>(items.size());
			addItems(items);
		}
	}

	private static final long serialVersionUID = 1L;

	//

	public List<T> getList() {

		return list;
	}

	public void addItem(final T item) {

		list.add(item);
	}

	@SuppressWarnings("unchecked")
	public void addItems(final T... items) {

		Collections.addAll(list, items);
	}

	public void addItems(final Collection<T> items) {

		list.addAll(items);
	}

}
