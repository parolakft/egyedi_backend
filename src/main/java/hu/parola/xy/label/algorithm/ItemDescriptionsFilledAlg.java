// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A cikkekhez milyen arányban vannak megadva az
 * allergének
 */
public class ItemDescriptionsFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(item_id) a, COUNT(NULLIF(description, '')) b FROM items WHERE NOT deleted";
	}

	@Override
	public String getFunction() {
		return "ileiras";
	}

}
