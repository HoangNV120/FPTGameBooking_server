package com.server.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomGenerator {
    public static String randomToString() {
        return RandomStringUtils.randomNumeric(8);
    }

    public static String randomPasswordToString() {
        return RandomStringUtils.random(8, true, true) + "!@#$%^&*".charAt((int) (Math.random() * 8));
    }
}
