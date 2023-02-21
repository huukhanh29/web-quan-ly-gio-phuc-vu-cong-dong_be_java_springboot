package com.beton408.security;


import java.text.Normalizer;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helpers {
    public static float calculateSimilarity(String str1, String str2) {
        String[] words1 = str1.split(" ");
        String[] words2 = str2.split(" ");
        int matches = 0;
        for (String word1 : words1) {
            if (Arrays.stream(words2).anyMatch(word1::equals)) {
                matches++;
            }
        }
        return (float) matches / words2.length;
    }

    public static String createSlug(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        return temp.replaceAll("[^\\p{ASCII}]", "");
    }

}
