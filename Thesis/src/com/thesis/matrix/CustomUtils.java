package com.thesis.matrix;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class CustomUtils {
    public static ArrayRealVector getMainDiagonal(RealMatrix m) {
        double[] tempArr = new double[m.getRowDimension()];
        for (int i = 0; i < m.getColumnDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    tempArr[i] = m.getEntry(i, j);
                }
            }
        }
        return new ArrayRealVector(tempArr, false);
    }

    public static RealMatrix getDiag(RealMatrix m) {
        int dimension = m.getRowDimension();
        double[][] tempArr = new double[dimension][dimension];
        for (int i = 0; i < m.getColumnDimension(); i++) {
            for (int j = 0; j < m.getColumnDimension(); j++) {
                if (i == j) {
                    tempArr[i][j] = m.getEntry(i, j);
                }
            }
        }
        return new BlockRealMatrix(tempArr);
    }

    public static RealMatrix vectorToMatrix(RealVector v) {
        int dimension = v.getDimension();
        RealMatrix m = new BlockRealMatrix(dimension, 1);
        for (int i = 0; i < dimension; i++) {
            m.setEntry(i, 0, v.getEntry(i));
        }
        return m;
    }

    public static RealMatrix getL(RealMatrix A) {
        return getDiag(A).subtract(A);
    }
}
