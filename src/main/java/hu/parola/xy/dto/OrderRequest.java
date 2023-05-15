// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

/**
 *
 */
public class OrderRequest extends IdRequest {

	private Integer order;

	//

	public OrderRequest() {
		super();
	}

	//

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

}
