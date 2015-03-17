package com.thesis.helper;

import org.jblas.DoubleMatrix;

public class DistancesHelper {
    public static DoubleMatrix chainGraph = new DoubleMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    public static DoubleMatrix triangleGraph = new DoubleMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 1},
            {0, 1, 1, 0}
    });

    public static DoubleMatrix treeMatrix = new DoubleMatrix(new double[][] {
            {0, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 1, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 1, 1, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
    });

    public static boolean equalDouble(double a, double b) {
        return Math.abs(a - b) < 0.0011;
    }

    public static boolean equalDoubleStrict(double a, double b) {
        return Math.abs(a - b) < 0.0000002;
    }

    public static boolean equalDoubleNonStrict(double a, double b) {
        return Math.abs(a - b) < 0.013;
    }
}
