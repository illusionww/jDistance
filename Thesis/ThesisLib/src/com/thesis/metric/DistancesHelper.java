package com.thesis.metric;

import org.jblas.DoubleMatrix;
import org.jblas.NativeBlas;
import org.jblas.exceptions.LapackArgumentException;
import org.jblas.exceptions.LapackConvergenceException;
import org.jblas.exceptions.SizeException;

import static org.jblas.util.Functions.log2;
import static org.jblas.util.Functions.max;
import static org.jblas.util.Functions.min;

public class DistancesHelper {
    public static DoubleMatrix solveLeastSquares(DoubleMatrix A, DoubleMatrix B) {
        if (B.rows < A.columns) {
            DoubleMatrix X = DoubleMatrix.concatVertically(B, new DoubleMatrix(A.columns - B.rows, B.columns));
            synchronized (DistancesHelper.class) {
                gelsd(A.dup(), X);
            }
            return X;
        } else {
            DoubleMatrix X = B.dup();
            synchronized (DistancesHelper.class) {
                gelsd(A.dup(), X);
            }
            return X.getRange(0, A.columns, 0, B.columns);
        }
    }

    public static DoubleMatrix pinv(DoubleMatrix A) {
        return solveLeastSquares(A, DoubleMatrix.eye(A.rows));
    }

    public static void gelsd(DoubleMatrix A, DoubleMatrix B) {
        int m = A.rows;
        int n = A.columns;
        int nrhs = B.columns;
        int minmn = min(m, n);
        int maxmn = max(m, n);

        if (B.rows < maxmn) {
            throw new SizeException("Result matrix B must be padded to contain the solution matrix X!");
        }

        int smlsiz;
        synchronized (DistancesHelper.class) {
            smlsiz = NativeBlas.ilaenv(9, "DGELSD", "", m, n, nrhs, 0);
        }
        int nlvl = max(0, (int) log2(minmn / (smlsiz + 1)) + 1);

        int[] iwork = new int[3 * minmn * nlvl + 11 * minmn];
        double[] s = new double[minmn];
        int[] rank = new int[1];
        int info;
        synchronized (DistancesHelper.class) {
            info = NativeBlas.dgelsd(m, n, nrhs, A.data, 0, m, B.data, 0, B.rows, s, 0, -1, rank, 0, iwork, 0);
        }
        if (info < 0) {
            throw new LapackArgumentException("DGESD", -info);
        } else if (info > 0) {
            throw new LapackConvergenceException("DGESD", info + " off-diagonal elements of an intermediat bidiagonal form did not converge to 0.");
        }
    }
}
