package com.thesis.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static float round(float result) {
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}
