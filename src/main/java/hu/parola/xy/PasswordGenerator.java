// ------------------------------------
// -------- Parola Kft ----------------
// -------- info@parola.hu ------------
// ------------------------------------

package hu.parola.xy;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    private static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";

    /**
     * Method will generate random string based on the parameters
     *
     * @param len
     *            the length of the random string
     * @param dic
     *            the dictionary used to generate the password
     * @return the random password
     */
    private static String generatePassword(int len, String dic) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result.append(dic.charAt(index));
        }
        return result.toString();
    }

    public static String generateCharacterPassword(int len) {
        return generatePassword(len, ALPHA_CAPS + ALPHA);
    }

    public static String generateAlphanumericPassword(int len) {
        return generatePassword(len, ALPHA_CAPS + ALPHA + NUMERIC);
    }


    public static String generateFullPassword(int len) {
        return generatePassword(len, ALPHA + ALPHA_CAPS + SPECIAL_CHARS + NUMERIC);
    }
}
