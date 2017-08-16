package com.projects.taxiservice.taxilogic.utilities;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Generates a random hash
 */
public class RandomTokenGen {
    private SecureRandom secureRandom = null;

    /**
     * Generates a random hash using SecureRandom and BigInteger
     *
     * @return a new generated hash
     */
    public String getSecureToken(){
        if(secureRandom == null) secureRandom = new SecureRandom();

        return new BigInteger(130, secureRandom).toString(32);
    }
}
