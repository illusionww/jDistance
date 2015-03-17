package com.thesis.metric;

import Jama.Matrix;
import jeigen.DenseMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.NativeBlas;
import org.jblas.exceptions.LapackArgumentException;
import org.jblas.exceptions.LapackConvergenceException;
import org.jblas.exceptions.SizeException;

import static org.jblas.util.Functions.log2;
import static org.jblas.util.Functions.max;
import static org.jblas.util.Functions.min;

public class DistancesHelper {
    public static DoubleMatrix pinv(DoubleMatrix A) {
        double[][] matrx = A.toArray2();
        Matrix matrix = new Matrix(matrx);
        return new DoubleMatrix(matrix.inverse().getArray());
    }

    public static synchronized DoubleMatrix mexp(DoubleMatrix A) {
        DenseMatrix dm = new DenseMatrix(A.toArray2()); // create new matrix
        DenseMatrix result = dm.mexp();
        return denseToDoubleMatrix(result);
    }

    public static DoubleMatrix denseToDoubleMatrix(DenseMatrix dm) {
        double[] values = dm.getValues();
        int length = values.length;
        int side = (int) Math.sqrt(length);

        double[][] newValues = new double[side][side];
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                newValues[i][j] = values[i * side + j];
            }
        }

        return new DoubleMatrix(newValues);
    }
}
