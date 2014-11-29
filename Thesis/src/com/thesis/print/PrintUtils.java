package com.thesis.print;

import org.jblas.FloatMatrix;

import java.util.Arrays;

public class PrintUtils {
    public static void printArray(FloatMatrix m, String name) {
        System.out.println(name + ":");
        printArray(m);
    }

    public static void printArray(FloatMatrix m) {
        String s = Arrays.deepToString(m.toArray2());
        String o = s.replaceAll("\\], \\[", "]\n[").replaceAll("\\[\\[", "[")
                .replaceAll("\\]\\]", "]").replaceAll(", ", ",\t");
        System.out.println(o);
        System.out.println();
    }
}
