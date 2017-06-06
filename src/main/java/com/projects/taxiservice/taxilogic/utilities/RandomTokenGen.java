package com.projects.taxiservice.taxilogic.utilities;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by O'Neill on 6/6/2017.
 */
public class RandomTokenGen {
    private static SecureRandom secureRandom = null;
    private static char[] symbols;

    public static String getSecureToken(){
        if(secureRandom == null) secureRandom = new SecureRandom();

        return new BigInteger(130, secureRandom).toString(32);
    }

    public static String getUnsecureToken(){
        if(symbols == null || symbols.length < 1) {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ch++) {
                tmp.append(ch);
            }
            for (char ch = 'a'; ch <= 'z'; ch++) {
                tmp.append(ch);
            }
            symbols = tmp.toString().toCharArray();
        }

        final Random random = new Random();

        char[] buf = new char[26];

        for (int i = 0; i < buf.length; i++) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }
}
