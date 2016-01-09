package com.jdistance.utils;

import jeigen.DenseMatrix;

public class MatrixUtils {
    public static double[][] toArray2(DenseMatrix dm) {
        double[][] values = new double[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(dm.getValues(), i * dm.rows, values[i], 0, dm.rows);
        }
        return values;
    }
}
