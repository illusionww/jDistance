package com.thesis.utils;

import jeigen.DenseMatrix;

public class MatrixAdapter {
    public static double[][] toArray2(DenseMatrix dm) {
        double[] values = dm.getValues();
        double[][] newValues = new double[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }
}
