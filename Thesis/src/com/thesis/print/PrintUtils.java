package com.thesis.print;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

public class PrintUtils {
    public static void printArray(RealMatrix m) {
        String s = Arrays.deepToString(m.getData());
        String o = s.replaceAll("\\], \\[", "]\n[").replaceAll("\\[\\[", "[")
                .replaceAll("\\]\\]", "]").replaceAll(", ", ",\t");
        System.out.println(o);
    }
}
