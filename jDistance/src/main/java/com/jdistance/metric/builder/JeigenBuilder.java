package com.jdistance.metric.builder;

import com.keithschwarz.johnsons.JohnsonsAlgorithm;
import com.jdistance.utils.MatrixAdapter;
import jeigen.DenseMatrix;

import java.util.function.UnaryOperator;

import static jeigen.Shortcuts.*;

public class JeigenBuilder {
    public DenseMatrix getL(DenseMatrix A) {
        return diag(A.sumOverRows().t()).sub(A);
    }

    // H = log(H0)
    public DenseMatrix H0toH(DenseMatrix H0) {
        return log(H0);
    }

    // H = (L + J)^{-1}
    public DenseMatrix getHResistance(DenseMatrix L) {
        int d = L.cols;
        double j = 1.0 / d;
        DenseMatrix J = ones(d, d).mul(j);
        DenseMatrix H = pinv(L.add(J));
        return H.mul(2); // normalization
    }

    // H0 = (I - tA)^{-1}
    public DenseMatrix getH0Walk(DenseMatrix A, double t) {
        int d = A.cols;
        DenseMatrix I = eye(d);
        DenseMatrix ins = I.sub(A.mul(t));
        return pinv(ins);
    }

    // H0 = (I + tL)^{-1}
    public DenseMatrix getH0Forest(DenseMatrix L, double t) {
        int d = L.cols;
        DenseMatrix I = eye(d);
        return pinv(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public DenseMatrix getH0Communicability(DenseMatrix A, double t) {
        return A.mul(t).mexp();
    }

    // H0 = exp(tA)
    public DenseMatrix getH0DummyCommunicability(DenseMatrix A, double t) {
        return dummy_mexp(A.mul(t), 30);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public DenseMatrix getD(DenseMatrix H) {
        int d = H.cols;
        DenseMatrix h = diagToVector(H);
        DenseMatrix i = DenseMatrix.ones(d, 1);
        return h.mmul(i.t()).add(i.mmul(h.t())).sub(H).sub(H.t()).div(2);
    }

    // Johnson's Algorithm
    public DenseMatrix getDShortestPath(DenseMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    public DenseMatrix getDFreeEnergy(DenseMatrix A, double beta) {
        int d = A.cols;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DenseMatrix e = ones(d, 1);
        DenseMatrix D = diag(A.mmul(e));
        DenseMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} (element-wise)* exp(-βC)
        DenseMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DenseMatrix W = Pref.mul(exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DenseMatrix I = eye(d);
        DenseMatrix Z = pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DenseMatrix Dh = diag(diagToVector(Z));
        DenseMatrix Zh = Z.mmul(pinv(Dh));

        // Φ = -1/β * log(Z^h)
        DenseMatrix F = log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DenseMatrix FE = F.add(F.t()).div(2);

        return FE.sub(diag(diagToVector(FE)));
    }

    public DenseMatrix sqrtD(DenseMatrix D) {
        return D.sqrt();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // HELPER METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static DenseMatrix log(DenseMatrix A) {
        return elementWise(A, Math::log);
    }

    public static DenseMatrix exp(DenseMatrix A) {
        return elementWise(A, Math::exp);
    }

    private static DenseMatrix elementWise(DenseMatrix A, UnaryOperator<Double> operator) {
        double[][] values = MatrixAdapter.toArray2(A);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = operator.apply(values[i][j]);
            }
        }
        return new DenseMatrix(values);
    }

    public static DenseMatrix diagToVector(DenseMatrix A) {
        DenseMatrix diag = new DenseMatrix(A.rows, 1);
        double[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    public static DenseMatrix pinv(DenseMatrix A) {
        if (A.cols != A.rows) {
            throw new RuntimeException("pinv matrix size error: must be square matrix");
        }

        return A.fullPivHouseholderQRSolve(diag(ones(A.cols, 1)));
    }

    public static DenseMatrix dummy_mexp(DenseMatrix A, int nSteps) {
        DenseMatrix runtot = eye(A.rows);
        DenseMatrix sum = eye(A.rows);

        double factorial = 1;
        for (int i = 1; i <= nSteps; i++) {
            factorial /= (double) i;
            sum = sum.mmul(A);
            runtot = runtot.add(sum.mul(factorial));
        }
        return runtot;
    }

    public static DenseMatrix normalization(DenseMatrix dm) {
        Double avg = dm.sum().sum().s() / (dm.cols * (dm.cols - 1));
        return dm.div(avg);
    }
}
