package com.thesis.matrix;

import org.jblas.FloatMatrix;

public class ElementWise {
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
