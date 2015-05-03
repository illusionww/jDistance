package com.thesis;

import com.thesis.helper.TestHelperLib;
import com.thesis.utils.BDMatrix;
import com.thesis.utils.MatrixAdapter;
import jeigen.DenseMatrix;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BigDecimalMatrixTest {
    private double[][] m1;
    private double[][] m2;
    private double[][] m3;
    private double[][] m4;
    private double[][] m5;

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
        m5 = new double[][]{
                {1, 0, 1},
                {0, 1, 3},
                {1, 1, 4},
                {2, 4, 3}
        };
    }

    @Test
    public void testAdd() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m2 = new BDMatrix(this.m2);
        BDMatrix m3 = new BDMatrix(this.m3);
        BDMatrix res = m1.add(m2).add(m3);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m2d = new DenseMatrix(this.m2);
        DenseMatrix m3d = new DenseMatrix(this.m3);
        DenseMatrix resd = m1d.add(m2d).add(m3d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMMul() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m2 = new BDMatrix(this.m2);
        BDMatrix m3 = new BDMatrix(this.m3);
        BDMatrix res = m1.mmul(m2).mmul(m3);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m2d = new DenseMatrix(this.m2);
        DenseMatrix m3d = new DenseMatrix(this.m3);
        DenseMatrix resd = m1d.mmul(m2d).mmul(m3d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMMulNotSquare() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m4 = new BDMatrix(this.m4);
        BDMatrix res = m1.mmul(m4);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m4d = new DenseMatrix(this.m4);
        DenseMatrix resd = m1d.mmul(m4d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMMulNotSquare2() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m5 = new BDMatrix(this.m5);
        BDMatrix res = m5.mmul(m1);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix m5d = new DenseMatrix(this.m5);
        DenseMatrix resd = m5d.mmul(m1d);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMul() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix res = m1.mul(4).mul(2);

        DenseMatrix m1d = new DenseMatrix(this.m1);
        DenseMatrix resd = m1d.mul(4).mul(2);

        assertTrue("Not equal", res.equals(resd));
    }

    @Test
    public void testMExp0() {
        BDMatrix m1 = new BDMatrix(new double[][]{{0.0}});
        m1 = m1.mexp(10);
        BDMatrix i = BDMatrix.eye(1);
        assertTrue("Not equal", i.equals(m1));
    }

    @Test
    public void testMExpMulInverse() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m2 = new BDMatrix(this.m1).mul(-1.0);
        m1 = m1.mexp(100);
        m2 = m2.mexp(100);
        BDMatrix res = m1.mmul(m2);
        BDMatrix i = BDMatrix.eye(3);

        double[][] m2array = MatrixAdapter.toDoubleArray2(res);
        double[][] m2darray = MatrixAdapter.toDoubleArray2(i);
        assertTrue("Not equal", TestHelperLib.equalArraysStrict(m2array, m2darray));
    }

    @Test
    public void testMExpMMul() {
        BDMatrix m1 = new BDMatrix(this.m1);
        BDMatrix m2 = new BDMatrix(this.m2);
        BDMatrix m3 = m1.add(m2);
        m1 = m1.mexp(10000);
        m2 = m2.mexp(10000);
        m3 = m3.mexp(10000);
        BDMatrix res = m1.mmul(m2);

        double[][] m2array = MatrixAdapter.toDoubleArray2(res);
        double[][] m2darray = MatrixAdapter.toDoubleArray2(m3);
        assertTrue("Not equal", TestHelperLib.equalArraysNonStrict(m2array, m2darray));
    }

    @Test
    public void testMExp() {
        BDMatrix m1 = new BDMatrix(this.m2);
        m1 = m1.mexp(10000);
        DenseMatrix m2 = new DenseMatrix(this.m2);
        m2 = m2.mexp();

        double[][] m2array = MatrixAdapter.toDoubleArray2(m1);
        double[][] m2darray = MatrixAdapter.toArray2(m2);
        assertTrue("Not equal", TestHelperLib.equalArraysStrict(m2array, m2darray));
    }
}
