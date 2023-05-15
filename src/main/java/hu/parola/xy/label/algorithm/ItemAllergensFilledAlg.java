// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A cikkekhez milyen arányban vannak megadva az
 * allergének
 */
public class ItemAllergensFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(item_id) a, COUNT(i_a_id) b  FROM ( SELECT item_id, MAX(item_allergen_id) AS i_a_id"
				+ "  FROM items LEFT JOIN items_allergens USING (item_id)  WHERE NOT deleted  GROUP BY item_id ) q";
	}

	@Override
	public String getFunction() {
		return "iall";
	}

}
