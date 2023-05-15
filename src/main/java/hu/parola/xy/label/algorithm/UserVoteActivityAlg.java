// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label.algorithm;

/**
 * Címkézési algoritmus: A felhasználó szavazati aktivitásának értéke
 */
public class UserVoteActivityAlg extends RatioAlgorithm {

	@Override
	protected String getSQL() {
		// XY-1029 IS NOT NULL helyett > 0...
		return "SELECT ( SELECT COUNT(vote_id) FROM votes WHERE user_id = ?1 AND item_id = ?2 AND vote > 0 ) a, COALESCE(MIN(db),0) b,"
				// XY-1029 Csak azokat vegyük figyelmbe, akik már címkézve lettek...
				+ " COALESCE(ROUND(AVG(db)),0)\\:\\:bigint c, COALESCE(MAX(db), 0) d  FROM ( SELECT COUNT(DISTINCT vote_id) AS db"
				+ "  FROM votes  JOIN votes_labels USING (vote_id)  WHERE item_id = ?2 AND full_label LIKE 'vuaktiv%'  GROUP BY user_id ) q";
	}

	@Override
	public String getFunction() {
		return "vuaktiv";
	}

}
