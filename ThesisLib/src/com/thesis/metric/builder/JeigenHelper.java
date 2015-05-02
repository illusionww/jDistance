package com.thesis.metric.builder;

import jeigen.DenseMatrix;

import java.util.function.UnaryOperator;

import static jeigen.Shortcuts.diag;
import static jeigen.Shortcuts.ones;

public class JeigenHelper {
    public static DenseMatrix log(DenseMatrix A) {
        return elementWise(A, Math::log);
    }

    public static DenseMatrix exp(DenseMatrix A) {
        return elementWise(A, Math::exp);
    }

    private static DenseMatrix elementWise(DenseMatrix A, UnaryOperator<Double> operator) {
        double[][] values = toArray2(A);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = operator.apply(values[i][j]);
            }
        }
        return new DenseMatrix(values);
    }

    public static DenseMatrix diagToVector(DenseMatrix A) {
        DenseMatrix diag = new DenseMatrix(A.rows, 1);
        double[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    public static DenseMatrix pinv(DenseMatrix A) {
        if (A.cols != A.rows) {
            throw new RuntimeException("pinv matrix size error: must be square matrix");
        }

        return A.fullPivHouseholderQRSolve(diag(ones(A.cols, 1)));
    }

    public static DenseMatrix normalization(DenseMatrix dm) {
        Double avg = dm.sum().sum().s() / (dm.cols * (dm.cols - 1));
        return dm.div(avg);
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
