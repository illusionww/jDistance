package com.thesis.helper;

import jeigen.DenseMatrix;

public class TestHelperLib {
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

    public static boolean equalArraysStrict(double[][] a, double[][] b) {
        if (a == null || b == null || a.length == 0 || b.length == 0 || a.length != b.length || a[0].length != b[0].length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (!equalDoubleStrict(a[i][j], b[i][j])) {
                    return false;
                }
            }
        }

        return true;
    }
}
