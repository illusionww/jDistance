package com.jdistance.learning.measure.helpers;

import jeigen.DenseMatrix;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import java.util.Arrays;

import static jeigen.DenseMatrix.*;

public class Shortcuts {
    public static DenseMatrix getL(DenseMatrix A) {
        return diag(A.sumOverRows().t()).sub(A);
    }

    public static DenseMatrix normalize(DenseMatrix dm) {
        double deviation = new StandardDeviation().evaluate(dm.getValues());
        return dm.div(deviation);
    }

    // H = element-wise log(H0)
    public static DenseMatrix H0toH(DenseMatrix H0) {
        DenseMatrix H = H0.log();
        return NaNPolice(H);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public static DenseMatrix HtoD(DenseMatrix H) {
        int d = H.cols;
        DenseMatrix h = diagToVector(H);
        DenseMatrix i = ones(d, 1);
        DenseMatrix D = h.mmul(i.t()).add(i.mmul(h.t())).sub(H).sub(H.t()).div(2);
        return NaNPolice(D);
    }

    // K = -1/2 HΔH
    public static DenseMatrix DtoK(DenseMatrix D) {
        int size = D.rows;
        DenseMatrix H = DenseMatrix.eye(size).sub(ones(size, size).div(size));
        DenseMatrix K = H.mmul(D).mmul(H).mul(-0.5);
        return NaNPolice(K);
    }

    // Johnson's Algorithm
    public static DenseMatrix getD_SP(DenseMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    // H = (L + J)^{-1}
    public static DenseMatrix getH_R(DenseMatrix A) {
        DenseMatrix L = getL(A);
        int d = L.cols;
        double j = 1.0 / d;
        DenseMatrix J = ones(d, d).mul(j);
        DenseMatrix ins = L.add(J);
        return pinv(ins);
    }

    public static DenseMatrix getH_CCT(DenseMatrix A) {
        DenseMatrix I = eye(A.cols);
        DenseMatrix d = A.sumOverRows().t();
        DenseMatrix D05 = diag(d.pow(-0.5));
        DenseMatrix H = eye(A.cols).sub(ones(A.rows, A.cols).div(A.cols));
        double volG = sum(A);
        DenseMatrix M = D05.mmul(A.sub(d.mmul(d.t()).div(volG))).mmul(D05);
        return H.mmul(D05).mmul(M).mmul(pinv(I.sub(M))).mmul(M).mmul(D05).mmul(H);
    }

    public static DenseMatrix getD_RSP(DenseMatrix A, double beta) {
        int d = A.cols;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DenseMatrix e = ones(d, 1);
        DenseMatrix D = diag(A.mmul(e));
        DenseMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} ◦ exp(-βC); ◦ is element-wise *
        DenseMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DenseMatrix W = Pref.mul(C.mul(-beta).exp());

        // Z = (I - W)^{-1}
        DenseMatrix I = eye(d);
        DenseMatrix Z = pinv(I.sub(W));

        // S = (Z(C ◦ W)Z)÷Z; ÷ is element-wise /
        DenseMatrix S = Z.mmul(C.mul(W)).mmul(Z).div(Z);
        // C_ = S - e(d_S)^T; d_S = diag(S)
        DenseMatrix C_ = S.sub(e.mmul(diagToVector(S).t()));
        // Δ_RSP = (C_ + C_^T)/2
        DenseMatrix Δ_RSP = C_.add(C_.t()).div(2);

        return Δ_RSP.sub(diag(diagToVector(Δ_RSP)));
    }

    public static DenseMatrix getD_FE(DenseMatrix A, double beta) {
        int d = A.cols;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DenseMatrix e = ones(d, 1);
        DenseMatrix D = diag(A.mmul(e));
        DenseMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} (element-wise)* exp(-βC)
        DenseMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DenseMatrix W = Pref.mul(C.mul(-beta).exp());

        // Z = (I - W)^{-1}
        DenseMatrix I = eye(d);
        DenseMatrix Z = pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DenseMatrix Dh = diag(diagToVector(Z));
        DenseMatrix Zh = Z.mmul(pinv(Dh));

        // Φ = -1/β * log(Z^h)
        DenseMatrix Φ = Zh.log().div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DenseMatrix Δ_FE = Φ.add(Φ.t()).div(2);

        return Δ_FE.sub(diag(diagToVector(Δ_FE)));
    }

    public static DenseMatrix pinv(DenseMatrix A) {
        for (double item : A.getValues()) {
            if (Double.isNaN(item)) {
                return DenseMatrix.ones(A.cols, A.rows).mul(Double.NaN);
            }
        }
        return A.fullPivHouseholderQRSolve(diag(ones(A.cols, 1)));
    }

    private static DenseMatrix diagToVector(DenseMatrix A) {
        DenseMatrix diag = new DenseMatrix(A.rows, 1);
        double[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    private static DenseMatrix NaNPolice(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item) || Double.isInfinite(item)) {
                return ones(D.rows, D.cols).mul(Double.NaN);
            }
        }
        return D;
    }

    private static double sum(DenseMatrix x) {
        return Arrays.stream(x.getValues()).sum();
    }
}
