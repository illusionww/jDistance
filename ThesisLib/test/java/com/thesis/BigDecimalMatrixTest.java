package com.thesis;

import com.thesis.helper.TestHelperLib;
import com.thesis.metric.builder.JeigenHelper;
import com.thesis.utils.BigDecimalMatrix;
import jeigen.DenseMatrix;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BigDecimalMatrixTest {
    private double[][] m1;
    private double[][] m2;
    private double[][] m3;
    private double[][] m4;

    @Before
    public void prepare() {
        m1 = new double[][]{
                {0, 1, 1},
                {3, 1, 0},
                {0, 1, 2}
        };
        m2 = new double[][]{
                {1, 4, 5},
                {0, 3, 6},
                {5, 1, 7}
        };
        m3 = new double[][]{
                {1, 0, 1},
                {0, 1, 3},
                {1, 1, 4}
        };
        m4 = new double[][]{
                {1, 0, 1, 3},
                {0, 1, 3, 3},
                {1, 1, 4, 4}
        };
    }

    @Test
    public void testAdd() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix m2 = new BigDecimalMatrix(this.m2);
        BigDecimalMatrix m3 = new BigDecimalMatrix(this.m3);
        BigDecimalMatrix res = m1.add(m2).add(m3);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m2d = new DenseMatrix(this.m2);
        DenseMatrix m3d = new DenseMatrix(this.m3);
        DenseMatrix resd = m1d.add(m2d).add(m3d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMMul() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix m2 = new BigDecimalMatrix(this.m2);
        BigDecimalMatrix m3 = new BigDecimalMatrix(this.m3);
        BigDecimalMatrix res = m1.mmul(m2).mmul(m3);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m2d = new DenseMatrix(this.m2);
        DenseMatrix m3d = new DenseMatrix(this.m3);
        DenseMatrix resd = m1d.mmul(m2d).mmul(m3d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMMulNotSquare() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix m4 = new BigDecimalMatrix(this.m4);
        BigDecimalMatrix res = m1.mmul(m4);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m4d = new DenseMatrix(this.m4);
        DenseMatrix resd = m1d.mmul(m4d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMul() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix res = m1.mul(4).mul(2);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix resd = m1d.mul(4).mul(2);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMExp0() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(new double[][]{{0.0}});
        m1 = m1.mexp(10);
        BigDecimalMatrix i = BigDecimalMatrix.eye(1);
        assertTrue("Not equal", i.equals(m1));
    }

    @Test
    public void testMExpMulInverse() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix m2 = new BigDecimalMatrix(this.m1).mul(-1.0);
        m1 = m1.mexp(100);
        m2 = m2.mexp(100);
        BigDecimalMatrix res = m1.mmul(m2);
        BigDecimalMatrix i = BigDecimalMatrix.eye(3);

        double[][] m2array = res.getDoubleArray();
        double[][] m2darray = i.getDoubleArray();
        assertTrue("Not equal", TestHelperLib.equalArraysStrict(m2array, m2darray));
    }

    @Test
    public void testMExpMMul() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m1);
        BigDecimalMatrix m2 = new BigDecimalMatrix(this.m2);
        BigDecimalMatrix m3 = m1.add(m2);
        m1 = m1.mexp(10000);
        m2 = m2.mexp(10000);
        m3 = m3.mexp(10000);
        BigDecimalMatrix res = m1.mmul(m2);

        double[][] m2array = res.getDoubleArray();
        double[][] m2darray = m3.getDoubleArray();
        assertTrue("Not equal", TestHelperLib.equalArraysNonStrict(m2array, m2darray));
    }

    @Test
    public void testMExp() {
        BigDecimalMatrix m1 = new BigDecimalMatrix(this.m2);
        m1 = m1.mexp(10000);
        DenseMatrix m2 = new DenseMatrix(this.m2);
        m2 = m2.mexp();

        double[][] m2array = m1.getDoubleArray();
        double[][] m2darray = JeigenHelper.toArray2(m2);
        assertTrue("Not equal", TestHelperLib.equalArraysStrict(m2array, m2darray));
    }
}
