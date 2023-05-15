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
 * Az SQL lekérdezés két {@link Long} értéket ad vissza, ezek arányát
 * <b>lineárisan</b> leosztjuk az {@literal 1-5} tartományra:
 * 
 * <pre>
 * 1 = 00% -  20%
 * 2 = 20% -  40%
 * 3 = 40% -  60%
 * 4 = 60% -  80%
 * 5 = 80% - 100%
 * </pre>
 */
public abstract class PercentageAlgorithm extends Algorithm {

	/// Az arány: Letároljuk az első lekérdezéskor.
	protected Double arany = null;

	@Override
	public void reset() {
		super.reset();
		arany = null;
	}

	/**
	 * Lekérdező SQL: Két {@link Long} értéket ad vissza, a másodikat osztva az
	 * elsőve,l kapjuk meg az arányt.
	 * 
	 * @return SQL lekérdezés
	 */
	protected abstract String getSQL();

	/**
	 * Az arány: Letároljuk az első lekérdezéskor, utána csak felhasználjuk a
	 * meglévő értéket...
	 * 
	 * @return
	 */
	protected Double getArany() {
		if (null == arany) {
			final Object[] res = (Object[]) dao.createNativeQuery(getSQL()).getSingleResult();
			if (null == res || 2 != res.length) {
				arany = 0.;
				return arany;
			}
			final long one = ((BigInteger) res[0]).longValue();
			final long two = ((BigInteger) res[1]).longValue();
			// Figyelmbe kell venni, hogy bármelyik érték lehet 0 ...
			if (0 == one)
				arany = 0.;
			else if (0 == two)
				arany = 1.;
			else
				arany = (double) two / one;
		}
		return arany;
	}

	//

	@Override
	public VotesLabel run(Vote v) {

		return createLabel(v, 0 == getArany() ? 1 : (int) Math.ceil(getArany() * 5));
	}

}
