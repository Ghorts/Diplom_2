package test.data;

import org.apache.commons.lang3.RandomStringUtils;

public class GeneratorTestData {

    public static String getRandomString() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getRandomMail() {
        return RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    }

    public static String getIngredient() {
        return "61c0c5a71d1f82001bdaaa6d";
    }
}
