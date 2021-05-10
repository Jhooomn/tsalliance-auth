package eu.tsalliance.auth.utils;

import lombok.experimental.UtilityClass;

import java.util.Random;
@UtilityClass
public class RandomUtil {

    /**
     * Generate a random string
     * @param length Length of the resulting string
     * @return String
     */
    public static String generateRandomString(int length) {
        return generateStringFromCharset(length, "0123456789abcdef");
    }

    /**
     * Generate a random number as string
     * @param length Amount of digits
     * @return String
     */
    public static String generateRandomNumberString(int length) {
        return generateStringFromCharset(length, "0123456789");
    }

    /**
     * Generate a random number
     * @param length Amount of digits
     * @return Integer
     */
    public static int generateRandomNumber(int length) {
        return Integer.parseInt(generateStringFromCharset(length, "0123456789"));
    }

    /**
     * Generate a string with specific length from a given charset.
     * @param length Length of the resulting string
     * @param chars Charset to get chars from
     * @return String
     */
    private static String generateStringFromCharset(int length, String chars) {
        StringBuilder hash = new StringBuilder();

        for(int i = 0; i < length; i++) {
            int rndChar = new Random().nextInt(chars.length());
            hash.append(chars.charAt(rndChar));
        }

        return hash.toString();
    }

}
