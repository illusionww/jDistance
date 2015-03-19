package com.thesis.metric;

import jeigen.DenseMatrix;

import java.util.function.UnaryOperator;

import static jeigen.Shortcuts.*;

public class DistancesHelper {
    public static DenseMatrix log(DenseMatrix A) {
        return elementWise(A, Math::log);
    }

    public static DenseMatrix sqrt(DenseMatrix A) {
        return elementWise(A, Math::sqrt);
    }

    public static DenseMatrix exp(DenseMatrix A) {
        return elementWise(A, Math::exp);
    }

    private static DenseMatrix elementWise(DenseMatrix A, UnaryOperator<Double> operator) {
        double[] values = A.getValues();
        for (int i = 0; i < values.length; i++) {
            A.set(i, operator.apply(values[i]));
        }
        return A;
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

    public static double[][] toArray2(DenseMatrix dm) {
        double[] values = dm.getValues();
        double[][] newValues = new double[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }
}
