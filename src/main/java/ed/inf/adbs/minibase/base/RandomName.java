package ed.inf.adbs.minibase.base;

import java.util.Random;

/**
 * RandomName is a utility class for generating random names.
 */
public class RandomName {
    private static final String ALLOWED_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generates a random name.
     *
     * @return the random name
     */
    public static String generate() {
        int length = 5;
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLOWED_CHARS.charAt(random.nextInt(ALLOWED_CHARS.length())));
        }
        return sb.toString();
    }
}