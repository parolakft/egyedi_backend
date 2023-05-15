// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

import hu.parola.xy.entity.Vote;
import hu.parola.xy.entity.VotesLabel;

/**
 * Címkézési algoritmus: A felhasználók nemének arányban, (már akik megadták)
 */
public class UserGenderAlg extends PercentageAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT COUNT(NULLIF(gender,1)) a, COUNT(NULLIF(gender,2)) b  FROM users  WHERE active";
	}

	@Override
	public String getFunction() {
		return "unemar";
	}

	@Override
	public VotesLabel run(Vote v) {

		final int ertek;
		double arany = getArany();
		if (0 == arany)
			ertek = 3;
		else if (arany < .1)
			ertek = 1;
		else if (arany < .5)
			ertek = 2;
		else if (arany > 10.)
			ertek = 5;
		else if (arany > 2.)
			ertek = 4;
		else
			ertek = 3;
		return createLabel(v, ertek);
	}

}
