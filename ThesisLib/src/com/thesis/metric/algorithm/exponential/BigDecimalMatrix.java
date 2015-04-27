package com.thesis.metric.algorithm.exponential;

import com.thesis.metric.DistancesHelper;
import jeigen.DenseMatrix;

import java.math.BigDecimal;
import java.util.Arrays;

public class BigDecimalMatrix {
    private BigDecimal[][] matrix;

    public BigDecimalMatrix(int M, int N) {
        this.matrix = new BigDecimal[M][N];
    }

    public BigDecimalMatrix(double[][] matrix) {
        this.matrix = new BigDecimal[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                this.matrix[i][j] = new BigDecimal(matrix[i][j]);
            }
        }
    }

    public BigDecimalMatrix(BigDecimal[][] matrix) {
        this.matrix = matrix;
    }

    public static BigDecimalMatrix ones(int M) {
        BigDecimalMatrix id = new BigDecimalMatrix(M, M);

        for (int i = 0; i < id.getM(); i++) {
            for (int j = 0; j < id.getN(); j++) {
                if (i == j) id.getArray()[i][j] = BigDecimal.ONE;
                else id.getArray()[i][j] = BigDecimal.ZERO;
            }
        }
        return id;
    }

    public int getM() {
        return matrix.length;
    }

    public int getN() {
        return matrix[0].length;
    }

    public BigDecimal[][] getArray() {
        return matrix;
    }

    public double[][] getDoubleArray() {
        double[][] array = new double[getM()][getN()];
        for (int i = 0; i < getM(); i++) {
            for (int j = 0; j < getN(); j++) {
                array[j][i] = matrix[i][j].doubleValue();
            }
        }
        return array;
    }

    public BigDecimalMatrix add(BigDecimalMatrix m2) {
        BigDecimalMatrix m3 = new BigDecimalMatrix(this.getM(), m2.getN());
        for (int i = 0; i < m3.getM(); i++) {
            for (int j = 0; j < m3.getN(); j++) {
                m3.getArray()[i][j] = this.getArray()[i][j].add(m2.getArray()[i][j]);
            }
        }
        return m3;
    }

    public BigDecimalMatrix mul(double scale) {
        BigDecimalMatrix res = new BigDecimalMatrix(this.getM(), this.getN());
        for (int i = 0; i < res.getM(); i++) {
            for (int j = 0; j < res.getN(); j++) {
                res.getArray()[i][j] = this.getArray()[i][j].multiply(new BigDecimal(scale));
            }
        }
        return res;
    }

    public BigDecimalMatrix mmul(BigDecimalMatrix m2) {
        if (this.getN() != m2.getM()) return null;

        BigDecimalMatrix res = new BigDecimalMatrix(this.getM(), m2.getN());

        for (int i = 0; i < this.getM(); i++) {
            for (int j = 0; j < m2.getN(); j++) {
                res.getArray()[i][j] = BigDecimal.ZERO;
                for (int k = 0; k < this.getN(); k++) {
                    res.getArray()[i][j] = res.getArray()[i][j].add(this.getArray()[i][k].multiply(m2.getArray()[k][j]));
                }
            }
        }
        return res;
    }

    public BigDecimalMatrix mexp(int nSteps) {
        BigDecimalMatrix runtot = ones(this.getM());
        BigDecimalMatrix sum = ones(this.getM());

        double factorial = 1.0;
        for (int i = 1; i <= nSteps; i++) {
            factorial /= (double) i;
            sum = sum.mmul(this);
            runtot = runtot.add(sum.mul(factorial));
        }
        return runtot;
    }

    public boolean equals(DenseMatrix d) {
        double[][] m2 = DistancesHelper.toArray2(d);
        if (this.getM() != m2.length || this.getN() != m2[0].length) {
            return false;
        }
        for (int i = 0; i < this.getM(); i++) {
            for (int j = 0; j < this.getN(); j++) {
                if (this.getArray()[j][i].doubleValue() != m2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BigDecimalMatrix)) {
            return false;
        }
        BigDecimalMatrix m2 = (BigDecimalMatrix) obj;
        if (this.getM() != m2.getM() || this.getN() != m2.getN()) {
            return false;
        }
        for (int i = 0; i < this.getM(); i++) {
            for (int j = 0; j < this.getN(); j++) {
                if (this.getArray()[i][j].doubleValue() != m2.getArray()[i][j].doubleValue()) {
                    return false;
                }
            }
        }
        return true;
    }
}
