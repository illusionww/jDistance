package com.thesis.helper;

import jeigen.DenseMatrix;

public class TestHelper {
    public static DenseMatrix chainGraph = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    public static DenseMatrix triangleGraph = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 1},
            {0, 1, 1, 0}
    });

    public static DenseMatrix treeMatrix = new DenseMatrix(new double[][] {
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
        return Math.abs(a - b) < 0.0000001;
    }

    public static boolean equalDoubleNonStrict(double a, double b) {
        return Math.abs(a - b) < 0.012;
    }
}
