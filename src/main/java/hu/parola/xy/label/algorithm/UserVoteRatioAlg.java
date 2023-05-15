// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A felhasználó szavazatainak mennyiség, aránya
 */
public class UserVoteRatioAlg extends RatioAlgorithm {

	@Override
	protected String getSQL() {
		return "SELECT ( SELECT COUNT(vote_id)  FROM votes  WHERE user_id = ?1 AND vote > 0 AND ?2 IS NOT NULL ) a, COALESCE(MIN(db), 0) b,"
				+ " COALESCE(ROUND(AVG(db)),0)\\:\\:bigint c, COALESCE(MAX(db),0) d  FROM ( SELECT COUNT(DISTINCT vote_id) AS db  FROM votes"
				// XY-1029 Csak azokat kell figyelembe venni, akik már címkézve is vannak...
				+ "  JOIN votes_labels USING (vote_id)  WHERE full_label LIKE 'vumenny%'  GROUP BY user_id ) q";
	}

	@Override
	public String getFunction() {
		return "vumenny";
	}

}
