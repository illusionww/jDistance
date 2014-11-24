package com.thesis.matrix;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class ElementWise {
    public static RealMatrix ln(final RealMatrix m) {
        double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = Math.log10(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    public static RealMatrix log(final RealMatrix m) {
        double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = Math.log(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    public static RealMatrix exp(final RealMatrix m) {
        double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = Math.exp(m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    public static RealMatrix inverseElements(final RealMatrix m) {
        double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); r++) {
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = 1.0 / (m.getEntry(r, c));
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }

    public static RealMatrix Pref(final RealMatrix m) {
        double[][] d = new double[m.getRowDimension()][m.getColumnDimension()];
        for (int r = 0; r < m.getRowDimension(); r++) {
            double sum = 0;
            for (int c = 0; c < m.getColumnDimension(); c++) {
                sum += m.getEntry(r, c);
            }
            for (int c = 0; c < m.getColumnDimension(); c++) {
                d[r][c] = (m.getEntry(r, c)) / sum;
            }
        }
        return new Array2DRowRealMatrix(d, false);
    }
}
