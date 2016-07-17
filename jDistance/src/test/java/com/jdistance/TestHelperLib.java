package com.jdistance;

import org.jblas.DoubleMatrix;

import java.util.function.BiFunction;

public class TestHelperLib {
    public final static DoubleMatrix chainGraph = new DoubleMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    public final static DoubleMatrix triangleGraph = new DoubleMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 1},
            {0, 1, 1, 0}
    });

    public final static DoubleMatrix fullGraph = new DoubleMatrix(new double[][]{
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

    public final static DoubleMatrix treeMatrix = new DoubleMatrix(new double[][]{
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

    public final static DoubleMatrix diplomaMatrix = new DoubleMatrix(new double[][]{
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

    public static double[][] toArray2(DoubleMatrix dm) {
        double[] values = dm.toArray();
        double[][] newValues = new double[dm.columns][dm.rows];
        for (int i = 0; i < dm.columns; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }
}
