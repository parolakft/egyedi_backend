// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A felhasználók milyen arányban adták meg a születési
 * idejüket
 */
public class UserAgeFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(user_id) a, COUNT(birthdate) b  FROM users  WHERE active";
	}

	@Override
	public String getFunction() {
		return "ukor";
	}

}
