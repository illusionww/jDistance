package com.thesis.utils;

import org.jblas.FloatMatrix;

import java.util.Collection;

public class MatrixUtils {
    private MatrixUtils() {}

    public static FloatMatrix getL(FloatMatrix A) {
        int rowsAmount = A.getRows();
        float[][] a = A.toArray2();
        float[] rowSums = new float[rowsAmount];
        for (int i = 0; i < rowsAmount; i++) {
            float rowSum = 0;
            for (float element : a[i]) {
                rowSum += element;
            }
            rowSums[i] = rowSum;
        }
        return FloatMatrix.diag(new FloatMatrix(rowSums)).sub(A);
    }
}
