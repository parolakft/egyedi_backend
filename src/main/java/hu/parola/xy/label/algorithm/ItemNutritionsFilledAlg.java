// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A cikkekhez milyen arányban vannak megadva az
 * allergének
 */
public class ItemNutritionsFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(item_id) a, COUNT(i_n_id) b  FROM ( SELECT items.item_id, MAX(nutrition_id) AS i_n_id"
				// XY-1029 N/A kihagyása...
				+ "  FROM items LEFT JOIN nutritions ON items.item_id = nutritions.item_id AND value <> 'N/A'"
				+ "  WHERE NOT deleted  GROUP BY items.item_id ) q";
	}

	@Override
	public String getFunction() {
		return "itap";
	}

}
