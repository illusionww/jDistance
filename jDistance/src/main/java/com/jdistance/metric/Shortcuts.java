package com.jdistance.metric;

import com.keithschwarz.johnsons.JohnsonsAlgorithm;
import jeigen.DenseMatrix;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import java.util.function.UnaryOperator;

import static jeigen.Shortcuts.*;

public class Shortcuts {
    public static DenseMatrix getL(DenseMatrix A) {
        return diag(A.sumOverRows().t()).sub(A);
    }

    public static DenseMatrix normalize(DenseMatrix dm) {
        double deviation = new StandardDeviation().evaluate(dm.getValues());
        return dm.div(deviation);
    }

    static DenseMatrix pinv(DenseMatrix A) {
        if (A.cols != A.rows) {
            throw new RuntimeException("pinv matrix size error: must be square matrix");
        }

        for (double item : A.getValues()) {
            if (Double.isNaN(item)) {
                return DenseMatrix.ones(A.cols, A.rows).mul(Double.NaN);
            }
        }

        return A.fullPivHouseholderQRSolve(diag(ones(A.cols, 1)));
    }

    // H = element-wise log(H0)
    static DenseMatrix H0toH(DenseMatrix H0) {
        return log(H0);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public static DenseMatrix HtoD(DenseMatrix H) {
        int d = H.cols;
        DenseMatrix h = diagToVector(H);
        DenseMatrix i = DenseMatrix.ones(d, 1);
        return h.mmul(i.t()).add(i.mmul(h.t())).sub(H).sub(H.t()).div(2);
    }

    // K = -1/2 HΔH
    static DenseMatrix DtoK(DenseMatrix D) {
        int size = D.rows;
        DenseMatrix H = DenseMatrix.eye(size).sub(DenseMatrix.ones(size, size).div(size));
        return H.mmul(D).mmul(H).mul(-0.5);
    }

    // K = -1/2 HΔ_(2)H
    static DenseMatrix DtoK_squared(DenseMatrix D) {
        int size = D.rows;
        DenseMatrix H = DenseMatrix.eye(size).sub(DenseMatrix.ones(size, size).div(size));
        return H.mmul(D.mul(D)).mmul(H).mul(-0.5);
    }

    // Johnson's Algorithm
    public static DenseMatrix getD_ShortestPath(DenseMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    // H = (L + J)^{-1}
    public static DenseMatrix getH_Resistance(DenseMatrix L) {
        int d = L.cols;
        double j = 1.0 / d;
        DenseMatrix J = ones(d, d).mul(j);
        DenseMatrix ins = L.add(J);
        return pinv(ins);
    }

    static DenseMatrix getD_RSP(DenseMatrix A, double beta) {
        int d = A.cols;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DenseMatrix e = ones(d, 1);
        DenseMatrix D = diag(A.mmul(e));
        DenseMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} ◦ exp(-βC); ◦ is element-wise *
        DenseMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DenseMatrix W = Pref.mul(exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DenseMatrix I = eye(d);
        DenseMatrix Z = pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DenseMatrix Dh = diag(diagToVector(Z));
        DenseMatrix Zh = Z.mmul(pinv(Dh));

        // S = (Z(C ◦ W)Z)÷Z; ÷ is element-wise /
        DenseMatrix S = Z.mmul(C.mul(W)).mmul(Z).div(Z);
        // C_ = S - e(d_S)^T; d_S = diag(S)
        DenseMatrix C_ = S.sub(e.mmul(diagToVector(S).t()));
        // Δ_RSP = (C_ + C_^T)/2
        DenseMatrix Δ_RSP = C_.add(C_.t()).div(2);

        return Δ_RSP.sub(diag(diagToVector(Δ_RSP)));
    }

    static DenseMatrix getD_FE(DenseMatrix A, double beta) {
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
        DenseMatrix Φ = log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DenseMatrix Δ_FE = Φ.add(Φ.t()).div(2);

        return Δ_FE.sub(diag(diagToVector(Δ_FE)));
    }

    private static DenseMatrix log(DenseMatrix A) {
        return elementWise(A, Math::log);
    }

    private static DenseMatrix exp(DenseMatrix A) {
        return elementWise(A, Math::exp);
    }

    private static DenseMatrix elementWise(DenseMatrix A, UnaryOperator<Double> operator) {
        double[] values = A.getValues();
        DenseMatrix newA = new DenseMatrix(A.rows, A.cols);
        for (int i = 0; i < values.length; i++) {
            newA.set(i, operator.apply(values[i]));
        }
        return newA;
    }

    private static DenseMatrix diagToVector(DenseMatrix A) {
        DenseMatrix diag = new DenseMatrix(A.rows, 1);
        double[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    private static DenseMatrix dummy_mexp(DenseMatrix A, int nSteps) {
        DenseMatrix totalSum = eye(A.rows);
        DenseMatrix currentElement = eye(A.rows);

        for (int i = 1; i <= nSteps; i++) {
            currentElement = currentElement.mmul(A.div(i));
            totalSum = totalSum.add(currentElement);
        }
        return totalSum;
    }
}
