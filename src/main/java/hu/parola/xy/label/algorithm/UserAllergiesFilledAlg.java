// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A felhasználók milyen arányban adták meg a allergiáikat
 */
public class UserAllergiesFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(user_id) a, COUNT(u_a_id) b FROM ( SELECT user_id, MAX(user_allergen_id) AS u_a_id"
				+ "  FROM users  LEFT JOIN users_allergens USING (user_id)  WHERE active  GROUP BY user_id ) q";
	}

	@Override
	public String getFunction() {
		return "uall";
	}

}
