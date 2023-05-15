// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

import hu.parola.xy.entity.Item;

/**
 * Címkézési algoritmus: A cikkekhez milyen arányban van kép feltöltve
 */
public class ItemImagesFilledAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return null; // Nem használt...
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Double getArany() {
		if (null == arany) {
			long[] count = { 0, 0 };
			final Item item = new Item();
			dao.createNativeQuery("SELECT item_id  FROM items  WHERE NOT deleted").getResultStream().forEach(i -> {
				++count[0];
				item.setId((Integer) i);
				if (item.isHasImage())
					++count[1];
			});
			if (0 == count[0])
				arany = 0.;
			else
				arany = (double) count[1] / count[0];
		}
		return arany;
	}

	@Override
	public String getFunction() {
		return "ikep";
	}

}
