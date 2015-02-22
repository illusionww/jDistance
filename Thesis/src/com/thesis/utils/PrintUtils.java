package com.thesis.utils;

import org.jblas.DoubleMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class PrintUtils {
    public static void print(DoubleMatrix m, String name) {
        System.out.println(arrayAsString(m.toArray2(), name));
        System.out.println();
    }

    public static String arrayAsString(double[][] m, String name) {
        return name + ":\n" + arrayAsString(m);
    }

    public static String arrayAsString(double[][] m) {
        String s = Arrays.deepToString(m);
        return s.replaceAll("\\], \\[", "]\n[").replaceAll("\\[\\[", "[")
                .replaceAll("\\]\\]", "]").replaceAll(", ", ",\t");
    }

    public static double round(double result) {
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}
