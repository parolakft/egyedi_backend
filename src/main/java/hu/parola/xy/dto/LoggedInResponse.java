// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.dto;

import hu.parola.xy.entity.User;

/**
 * Response adatszerkezet, bejelentkezett felhasználóval
 */
public class LoggedInResponse extends CommonResponse {

	private String token;
	private User loggedInUser;

	//

	public LoggedInResponse() {
		super();
	}

	public LoggedInResponse(User loggedInUser, Object token) {
		this();
		this.loggedInUser = loggedInUser;
		this.token = (String) token;
	}

	private static final long serialVersionUID = 1L;

	//

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public User getUser() {
		return loggedInUser;
	}

	public void setUser(final User user) {
		loggedInUser = user;
	}

}
