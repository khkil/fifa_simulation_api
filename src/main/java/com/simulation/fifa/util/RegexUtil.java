package com.simulation.fifa.util;

public class RegexUtil {
    public static String extractNumbers(String input) {
        return input.replaceAll("[^0-9]", "");
    }
}
