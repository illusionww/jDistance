package com.thesis.utils;

import jeigen.DenseMatrix;

import java.math.BigDecimal;

public class MatrixAdapter {
    public static DenseMatrix toDense(BDMatrix matrix) {
        return new DenseMatrix(toDoubleArray2(matrix));
    }

    public static BDMatrix toBD(DenseMatrix matrix) {
        return new BDMatrix(toArray2(matrix));
    }

    public static double[][] toArray2(DenseMatrix dm) {
        double[] values = dm.getValues();
        double[][] newValues = new double[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }

    public static double[][] toDoubleArray2(BDMatrix bdm) {
        double[][] array = new double[bdm.rows][bdm.cols];
        for (int i = 0; i < bdm.rows; i++) {
            for (int j = 0; j < bdm.cols; j++) {
                array[j][i] = bdm.get(i, j).doubleValue();
            }
        }
        return array;
    }

    public static BigDecimal[][] toArray2(BDMatrix bdm) {
        BigDecimal[] values = bdm.getValues();
        BigDecimal[][] newValues = new BigDecimal[bdm.cols][bdm.rows];
        for (int i = 0; i < bdm.cols; i++) {
            System.arraycopy(values, i * bdm.rows, newValues[i], 0, bdm.rows);
        }
        return newValues;
    }
}
