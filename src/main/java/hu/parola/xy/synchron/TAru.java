// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import hu.parola.xy.MagyarBooleanDeserializer;

/**
 * TAru, a parMAN rendszerből jön.
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TAru implements Serializable {

	@JsonProperty("taru_id")
	private Integer taruId;
	private String kod;
	@JsonDeserialize(using = MagyarBooleanDeserializer.class)
	private Boolean megjelenik;
	private String arumn;
	private String arumgj;
	private String aruosz;
	private String arufelt;
	private String felhut;
	private String aruano;
	private String arusly;
	private String arubsly;

	private String shortName;

	@JsonProperty("Allergének")
	private TAruAll allergenek;
	@JsonProperty("Tápértékek")
	private TAruTap tapertekek;
	@JsonProperty("Alapanyagok")
	private List<TAruNorm> alapanyagok;

	//

	public TAru() {
		super();
	}

	private static final long serialVersionUID = 1L;

	//

	public Integer getTaruId() {
		return taruId;
	}

	public void setTaruId(Integer taruId) {
		this.taruId = taruId;
	}

	public String getKod() {
		return kod;
	}

	public void setKod(String kod) {
		this.kod = kod;
	}

	public boolean isMegjelenik() {
		return null != megjelenik && megjelenik;
	}

	public void setMegjelenik(boolean megjelenik) {
		this.megjelenik = megjelenik;
	}

	public String getArumn() {
		return arumn;
	}

	public void setArumn(String arumn) {
		this.arumn = arumn;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getArumgj() {
		return arumgj;
	}

	public void setArumgj(String arumgj) {
		this.arumgj = arumgj;
	}

	public String getAruosz() {
		return aruosz;
	}

	public void setAruosz(String aruosz) {
		this.aruosz = aruosz;
	}

	public String getArufelt() {
		return arufelt;
	}

	public void setArufelt(String arufelt) {
		this.arufelt = arufelt;
	}

	public String getFelhut() {
		return felhut;
	}

	public void setFelhut(String felhut) {
		this.felhut = felhut;
	}

	public String getAruano() {
		return aruano;
	}

	public void setAruano(String aruano) {
		this.aruano = aruano;
	}

	public String getArusly() {
		return arusly;
	}

	public void setArusly(String arusly) {
		this.arusly = arusly;
	}

	public String getArubsly() {
		return arubsly;
	}

	public void setArubsly(String arubsly) {
		this.arubsly = arubsly;
	}

	public TAruAll getAllergenek() {
		return allergenek;
	}

	public void setAllergenek(TAruAll allergenek) {
		this.allergenek = allergenek;
	}

	public TAruTap getTapertekek() {
		return tapertekek;
	}

	public void setTapertekek(TAruTap tapertekek) {
		this.tapertekek = tapertekek;
	}

	public List<TAruNorm> getAlapanyagok() {
		return alapanyagok;
	}

	public void setAlapanyagok(List<TAruNorm> alapanyagok) {
		this.alapanyagok = alapanyagok;
	}

	//

	@Override
	public String toString() {
		return new StringBuilder(1000).append("TAru [id=").append(taruId).append(", kod=").append(kod)
				.append(", megjelenik=").append(megjelenik).append(", arumn=").append(arumn).append(", aruano=")
				.append(aruano).append(", arusly=").append(arusly).append(", arubsly=").append(arubsly).append("...]")
				.toString();
	}

}
