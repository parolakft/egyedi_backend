// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.parola.xy.ejb.LabelDao;
import hu.parola.xy.entity.Vote;
import hu.parola.xy.entity.VotesLabel;
import hu.parola.xy.label.algorithm.ItemAllergensFilledAlg;
import hu.parola.xy.label.algorithm.ItemDescriptionsFilledAlg;
import hu.parola.xy.label.algorithm.ItemImagesFilledAlg;
import hu.parola.xy.label.algorithm.ItemNutritionsFilledAlg;
import hu.parola.xy.label.algorithm.UserAgeFilledAlg;
import hu.parola.xy.label.algorithm.UserAllergiesFilledAlg;
import hu.parola.xy.label.algorithm.UserGenderFilledAlg;
import hu.parola.xy.label.algorithm.UserVoteActivityAlg;
import hu.parola.xy.label.algorithm.UserVoteRatioAlg;

public abstract class Algorithm {

	protected LabelDao dao;

	private static final Logger LOGGER = LoggerFactory.getLogger(Algorithm.class);

	//

	/**
	 * Az adott szavazatra generál 1db címkét.
	 * 
	 * @param v Szavazat
	 * @return Cimke a szavazathoz
	 */
	public abstract VotesLabel run(Vote v);

	/**
	 * Visszaadja a funkció nevét. Ez található az adatbázisban is.
	 * 
	 * @return Funkciónév
	 */
	protected abstract String getFunction();

	//

	/**
	 * Alaphelyzetbe állítja a címkéző algoritmust.
	 */
	public void reset() {
		priority = null;
	}

	//

	private Short priority = null;

	/**
	 * Lekérdezi a relevanciát az adatbázisból. Felhasználja a
	 * {@link #getFunction()} metódus értékét.
	 * 
	 * @return Relevancia szint (1-5)
	 */
	protected final Short getPriority() {
		if (null == priority)
			priority = dao.getRelevance(getFunction());
		return priority;
	}

	//

	private static final Set<Algorithm> ALGS = new HashSet<>();

	public static int count() {
		return ALGS.size();
	}

	public static void resetAll(final LabelDao dao) {
		// Algoritmusok példányosítása, ha még nincsenek példányosítva...
		if (ALGS.isEmpty()) {
			/// Cikk algoritmusok
			ALGS.add(new ItemAllergensFilledAlg()); // iall
			ALGS.add(new ItemDescriptionsFilledAlg()); // ileiras
			ALGS.add(new ItemImagesFilledAlg()); // ikep
			ALGS.add(new ItemNutritionsFilledAlg()); // itap

			/// Felhasználó algoritmusok
			ALGS.add(new UserAgeFilledAlg()); // ukor
			ALGS.add(new UserAllergiesFilledAlg()); // uall
			ALGS.add(new UserGenderFilledAlg()); // unem
			// Ez nincs specifikálva: Nemek egymáshoz viszonyított aránya...
			// ALGS.add(new UserGenderAlg()); // unemar

			/// Felhasználó-szavazat algoritmusok
			ALGS.add(new UserVoteActivityAlg()); // vuaktiv
			ALGS.add(new UserVoteRatioAlg()); // vumenny

			/// Még nem implementált algoritmusok:
			// vuszoras, vuketes,
			// vimenny, viaktiv, viszoras,
			// iuj, uuj, viujaktiv, vuujaktiv,
			// vutompit, viaktivint, liaktiv
		}
		ALGS.forEach(alg -> {
			alg.dao = dao;
			alg.reset();
		});
	}

	public static Stream<VotesLabel> generate(final Vote v) {
		try {
			return ALGS.stream().map(alg -> alg.run(v));
		} catch (final Exception ex) {
			LOGGER.error("", ex);
			return Stream.empty();
		}
	}

	//

	/**
	 * Létrehoz egy címkét, és hozzárendeli a szavazathoz, és visszaadja.
	 * <p>
	 * Szöveges címke létrehozása a paraméterek alapján:
	 * {@code funkciónév_forrás_prioritás_érték}, ahol
	 * <ul>
	 * <li>{@code funkciónév}: Az algoritmus funkció neve (lásd
	 * {@link #getFunction()} metódus)
	 * <li>{@code forrás}: Mindig 01=Egyedi címkéző rendszer
	 * <li>{@code prioritás}: A funkcióhoz rendelt relevancia (lásd
	 * {@link #getPriority()} metódus)
	 * <li>{@code érték}: A funkció által generált érték, spec. szerint
	 * {@literal 01} és {@literal 05} között
	 * </ul>
	 * </p>
	 * 
	 * @param v   Szavazat
	 * @param val Érték
	 * @return Letárolt címke
	 */
	protected final VotesLabel createLabel(final Vote v, final Integer val) {

		// Szöveges címke létrehozása a paraméterek alapján
		final String tag = String.format("%s_01_%02d_%02d", getFunction(), getPriority(), val);

		// Visszaadjuk a hívónak...
		final VotesLabel label = new VotesLabel();
		label.setVote(v);
		label.setFullLabel(tag);
		label.setCreated(LocalDateTime.now());
		return label;
	}

}
