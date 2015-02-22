package com.thesis.metric.hepler;

import org.jblas.FloatMatrix;
import org.jblas.Solve;

public class MatrixHelper {
    private MatrixHelper() {}

    public static FloatMatrix inverse(FloatMatrix m) {
        int d = m.getColumns();
        FloatMatrix I = FloatMatrix.eye(d);
        return Solve.solve(m, I);
    }
}
