// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A felhasználók milyen arányban adták meg a nemüket
 */
public class UserGenderFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		// XY-1029 A 0 (nem megadott) nem számít értéknek...
		return "SELECT COUNT(user_id) a, COUNT(NULLIF(gender, 0)) b  FROM users  WHERE active";
	}

	@Override
	public String getFunction() {
		return "unem";
	}

}
