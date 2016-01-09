package com.jdistance.helper;

import jeigen.DenseMatrix;

import java.util.function.BiFunction;

public class TestHelperLib {
    public final static DenseMatrix chainGraph = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    public final static DenseMatrix triangleGraph = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 1},
            {0, 1, 1, 0}
    });

    public final static DenseMatrix fullGraph = new DenseMatrix(new double[][]{
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 0, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 0, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 0}
    });

    public final static DenseMatrix treeMatrix = new DenseMatrix(new double[][]{
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

    public final static DenseMatrix diplomaMatrix = new DenseMatrix(new double[][]{
            {0, 1, 1, 0, 0, 0},
            {1, 0, 1, 0, 0, 0},
            {1, 1, 0, 1, 1, 0},
            {0, 0, 1, 0, 1, 1},
            {0, 0, 1, 1, 0, 1},
            {0, 0, 0, 1, 1, 0}
    });

    public static boolean equalDouble(double a, double b) {
        double min = Math.min(a, b) != 0 ? Math.min(a, b) : 1;
        return Math.abs(a - b) / min < 0.001;
    }

    public static boolean equalDoubleStrict(double a, double b) {
        double min = Math.min(a, b) != 0 ? Math.min(a, b) : 1;
        return Math.abs(a - b) / min < 0.000001;
    }

    public static boolean equalDoubleNonStrict(double a, double b) {
        double min = Math.min(a, b) != 0 ? Math.min(a, b) : 1;
        return Math.abs(a - b) / min < 0.013;
    }

    public static boolean equalArraysStrict(double[][] a, double[][] b) {
        return equalArrays(a, b, TestHelperLib::equalDoubleStrict);
    }

    public static boolean equalArrays(double[][] a, double[][] b, BiFunction<Double, Double, Boolean> operator) {
        if (a == null || b == null || a.length == 0 || b.length == 0 || a.length != b.length || a[0].length != b[0].length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (!operator.apply(a[i][j], b[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static double[][] toArray2(DenseMatrix dm) {
        double[] values = dm.getValues();
        double[][] newValues = new double[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }
}
