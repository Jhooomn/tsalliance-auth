package eu.tsalliance.auth.utils;

import java.util.Random;

public class RandomUtil {

    public static String generateRandomHash(int length) {
        String hexChars = "0123456789abcdef";
        StringBuilder hash = new StringBuilder();

        for(int i = 0; i < length; i++) {
            int rndChar = new Random().nextInt(hexChars.length());
            hash.append(hexChars.charAt(rndChar));
        }

        return hash.toString();
    }

}
