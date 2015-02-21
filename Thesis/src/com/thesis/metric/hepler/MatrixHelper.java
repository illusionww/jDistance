package com.thesis.metric.hepler;

import org.jblas.FloatMatrix;
import org.jblas.Solve;

public class MatrixHelper {
    private MatrixHelper() {}

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

    public static FloatMatrix inverse(FloatMatrix m) {
        int d = m.getColumns();
        FloatMatrix I = FloatMatrix.eye(d);
        return Solve.solve(m, I);
    }
}
