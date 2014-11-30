package com.thesis.matrix;

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

    public static FloatMatrix Pref(final FloatMatrix m) {
        float[][] d = new float[m.getRows()][m.getColumns()];
        for (int r = 0; r < m.getRows(); r++) {
            float sum = 0;
            for (int c = 0; c < m.getColumns(); c++) {
                sum += m.get(r, c);
            }
            for (int c = 0; c < m.getColumns(); c++) {
                d[r][c] = (m.get(r, c)) / sum;
            }
        }
        return new FloatMatrix(d);
    }
}
