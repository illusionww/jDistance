package com.thesis.utils;

import org.jblas.FloatMatrix;

import java.util.Arrays;

public class PrintUtils {
    public static void printArray(FloatMatrix m, String name) {
        printArray(m.toArray2(), name);
    }

    public static void printArray(FloatMatrix m) {
        printArray(m.toArray2());
    }

    public static void printArray(float[][] m, String name) {
        System.out.println(name + ":");
        printArray(m);
    }

    public static void printArray(float[][] m) {
        String s = Arrays.deepToString(m);
        String o = s.replaceAll("\\], \\[", "]\n[").replaceAll("\\[\\[", "[")
                .replaceAll("\\]\\]", "]").replaceAll(", ", ",\t");
        System.out.println(o);
        System.out.println();
    }
}
