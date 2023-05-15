// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.label;

public interface LabellerTask extends Runnable {

	boolean isRunning();
	
	String getLog();

}
