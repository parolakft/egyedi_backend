// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class TAruAll implements Serializable {

	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all01;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all02;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all03;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all04;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all05;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all06;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all07;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all08;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all09;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all10;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all11;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all12;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all13;
	@JsonFormat(shape = Shape.NUMBER_INT)
	private boolean all14;

	//

	public TAruAll() {
		super();
	}

	private static final long serialVersionUID = 1L;

	//

	public boolean isAll01() {
		return all01;
	}

	public void setAll01(boolean all01) {
		this.all01 = all01;
	}

	public boolean isAll02() {
		return all02;
	}

	public void setAll02(boolean all02) {
		this.all02 = all02;
	}

	public boolean isAll03() {
		return all03;
	}

	public void setAll03(boolean all03) {
		this.all03 = all03;
	}

	public boolean isAll04() {
		return all04;
	}

	public void setAll04(boolean all04) {
		this.all04 = all04;
	}

	public boolean isAll05() {
		return all05;
	}

	public void setAll05(boolean all05) {
		this.all05 = all05;
	}

	public boolean isAll06() {
		return all06;
	}

	public void setAll06(boolean all06) {
		this.all06 = all06;
	}

	public boolean isAll07() {
		return all07;
	}

	public void setAll07(boolean all07) {
		this.all07 = all07;
	}

	public boolean isAll08() {
		return all08;
	}

	public void setAll08(boolean all08) {
		this.all08 = all08;
	}

	public boolean isAll09() {
		return all09;
	}

	public void setAll09(boolean all09) {
		this.all09 = all09;
	}

	public boolean isAll10() {
		return all10;
	}

	public void setAll10(boolean all10) {
		this.all10 = all10;
	}

	public boolean isAll11() {
		return all11;
	}

	public void setAll11(boolean all11) {
		this.all11 = all11;
	}

	public boolean isAll12() {
		return all12;
	}

	public void setAll12(boolean all12) {
		this.all12 = all12;
	}

	public boolean isAll13() {
		return all13;
	}

	public void setAll13(boolean all13) {
		this.all13 = all13;
	}

	public boolean isAll14() {
		return all14;
	}

	public void setAll14(boolean all14) {
		this.all14 = all14;
	}

	@Override
	public String toString() {
		return String.format("[%B,%B,%B,%B,%B,%B,%B,%B,%B,%B,%B,%B,%B,%B]", all01, all02, all03, all04, all05, all06,
				all07, all08, all09, all10, all11, all12, all13, all14);
	}

}
