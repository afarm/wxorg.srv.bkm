package wxorg.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class RandomString {
    public static String random() {
        String generatedString = RandomStringUtils.randomAlphanumeric(8).toUpperCase(Locale.ROOT);
        System.out.println("generatedString = " + generatedString);
        return generatedString;
    }
}
