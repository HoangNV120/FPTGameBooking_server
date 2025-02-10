package com.server.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomGenerator {
    public static String randomToString() {
        return RandomStringUtils.randomNumeric(8);
    }
}
