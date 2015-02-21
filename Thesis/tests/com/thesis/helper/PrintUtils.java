package com.thesis.helper;

import java.util.Arrays;

public class PrintUtils {
    public static String arrayAsString(float[][] m, String name) {
        return name + ":\n" + arrayAsString(m);
    }

    public static String arrayAsString(float[][] m) {
        String s = Arrays.deepToString(m);
        return s.replaceAll("\\], \\[", "]\n[").replaceAll("\\[\\[", "[")
                .replaceAll("\\]\\]", "]").replaceAll(", ", ",\t");
    }
}
