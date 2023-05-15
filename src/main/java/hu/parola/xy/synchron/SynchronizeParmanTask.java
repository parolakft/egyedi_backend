// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy.synchron;

public interface SynchronizeParmanTask extends Runnable {

	boolean isRunning();

	String getLog();

}
