// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

/**
 *
 */
public class ReviewRequest extends IdRequest {

	private String text;

	//

	public ReviewRequest() {
		super();
	}

	//

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
