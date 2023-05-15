// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class TAruTap {

	@JsonProperty("Kalóriaérték (számított)")
	@JsonAlias({"kcal számított", "kcal"})
	protected String kCal;
	@JsonProperty("Energiaérték (számított)")
	@JsonAlias({"kJ számított", "kJ"})
	protected String energy;
	@JsonProperty("Zsír")
	protected String fat;
	@JsonProperty("tel.zsírs")
	protected String fatSat;
	@JsonProperty("e.tlenzsír")
	protected String fatUnsatMono;
	@JsonProperty("t.tlenzsír")
	protected String fetUnsatPoly;
	@JsonProperty("Szénh.")
	protected String carbon;
	@JsonProperty("cukrok")
	protected String sugar;
	@JsonProperty("poliolok")
	protected String polyols;
	@JsonProperty("kemenyítő")
	protected String starch;
	@JsonProperty("Rost")
	protected String fiber;
	@JsonProperty("Feh.")
	protected String protein;
	@JsonProperty("Só")
	protected String salt;

	//

	public TAruTap() {
		super();
	}

	//

	/// Nem teljesen szabványos, de a szinkronban kézzel összeállított getter-névhez így jó...
	public String getKCal() {
		return kCal;
	}

	public void setKCal(String kCal) {
		this.kCal = kCal;
	}

	public String getEnergy() {
		return energy;
	}

	public void setEnergy(String energy) {
		this.energy = energy;
	}

	public String getFat() {
		return fat;
	}

	public void setFat(String fat) {
		this.fat = fat;
	}

	public String getFatSat() {
		return fatSat;
	}

	public void setFatSat(String fatSat) {
		this.fatSat = fatSat;
	}

	public String getFatUnsatMono() {
		return fatUnsatMono;
	}

	public void setFatUnsatMono(String fatUnsatMono) {
		this.fatUnsatMono = fatUnsatMono;
	}

	public String getFetUnsatPoly() {
		return fetUnsatPoly;
	}

	public void setFetUnsatPoly(String fetUnsatPoly) {
		this.fetUnsatPoly = fetUnsatPoly;
	}

	public String getCarbon() {
		return carbon;
	}

	public void setCarbon(String carbon) {
		this.carbon = carbon;
	}

	public String getSugar() {
		return sugar;
	}

	public void setSugar(String sugar) {
		this.sugar = sugar;
	}

	public String getPolyols() {
		return polyols;
	}

	public void setPolyols(String polyols) {
		this.polyols = polyols;
	}

	public String getStarch() {
		return starch;
	}

	public void setStarch(String starch) {
		this.starch = starch;
	}

	public String getFiber() {
		return fiber;
	}

	public void setFiber(String fiber) {
		this.fiber = fiber;
	}

	public String getProtein() {
		return protein;
	}

	public void setProtein(String protein) {
		this.protein = protein;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

}
