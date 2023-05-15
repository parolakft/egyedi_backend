// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

import java.math.BigInteger;

import hu.parola.xy.entity.Vote;
import hu.parola.xy.entity.VotesLabel;
import hu.parola.xy.label.Algorithm;

/**
 * Címkézési algoritmus-ős:<br />
 * Az SQL lekérdezés négy {@link Long} értéket ad vissza: aktuális, min, átlag,
 * max. Ezek alapján osztjuk be az {@literal 1-5} tartományra:
 * 
 * <OL>
 * <li><b>0 - min</b> (lényegében első szavazata)</li>
 * <li><b>min - átlag</b></li>
 * <li><b>átlag</b></li>
 * <li><b>átlag - max</b></li>
 * <li><b>max feletti</b> (lényegében új max)</li>
 * </ol>
 */
public abstract class RatioAlgorithm extends Algorithm {

	/**
	 * Lekérdező SQL: Négy {@link Long} értéket ad vissza: aktuális, min, átlag,
	 * max.
	 * <p>
	 * Nem adhat vissza {@code NULL}-okat, de 0-ákat igen...
	 * </p>
	 * 
	 * @return SQL lekérdezés. 3 paramétert kap: user, item, alg
	 */
	protected abstract String getSQL();

	//

	private int getValue(final Vote v) {
		// Nem adhat vissza NULL-okat, de 0-t igen...
		final Object[] res = (Object[]) dao.createNativeQuery(getSQL()).setParameter(1, v.getUser().getId())
				.setParameter(2, v.getItem().getId()).getSingleResult();
		if (null == res || 4 != res.length) {
			return 1;
		}
		final long db = ((BigInteger) res[0]).longValue();
		if (0 == db)
			return 1;
		final long min = ((BigInteger) res[1]).longValue();
		final long avg = ((BigInteger) res[2]).longValue();
		final long max = ((BigInteger) res[3]).longValue();
		// < min
		if (db < min)
			return 1;
		// min < avg
		if (db < avg)
			return 2;
		// max >
		if (db > max)
			return 5;
		// avg > max
		if (db > avg)
			return 4;
		// = avg
		return 3;
	}

	@Override
	public VotesLabel run(Vote v) {
		return createLabel(v, getValue(v));
	}

}
