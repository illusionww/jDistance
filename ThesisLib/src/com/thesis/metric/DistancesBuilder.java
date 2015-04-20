package com.thesis.metric;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.thesis.metric.algorithm.johnsons.JohnsonsAlgorithm;
import jeigen.ComplexDenseMatrix;
import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.OptionalDouble;

import static jeigen.Shortcuts.*;

public class DistancesBuilder {
    // 0 < t < ρ^{-1} -> 0 < t < 1
    public double rho(DenseMatrix A, double t) {
        ComplexDenseMatrix cfm = new ComplexDenseMatrix(A.eig().values);
        double rho = cfm.abs().maxOverCols().s();
        return t / rho;
    }

    public DenseMatrix getL(DenseMatrix A) {
        return diag(A.sumOverRows().t()).sub(A);
    }

    // H = log(H0)
    public DenseMatrix H0toH(DenseMatrix H0) {
        return DistancesHelper.log(H0);
    }

    // H = (L + J)^{-1}
    public DenseMatrix getHResistance(DenseMatrix L) {
        int d = L.cols;
        double j = 1.0 / d;
        DenseMatrix J = ones(d, d).mul(j);
        DenseMatrix H = DistancesHelper.pinv(L.add(J));
        return H.mul(2); // normalization
    }

    // H0 = (I - tA)^{-1}
    public DenseMatrix getH0Walk(DenseMatrix A, double t) {
        int d = A.cols;
        DenseMatrix I = eye(d);
        DenseMatrix ins = I.sub(A.mul(t));
        return DistancesHelper.pinv(ins);
    }

    // H0 = (I + tL)^{-1}
    public DenseMatrix getH0Forest(DenseMatrix L, double t) {
        int d = L.cols;
        DenseMatrix I = eye(d);
        return DistancesHelper.pinv(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public DenseMatrix getH0Communicability(DenseMatrix A, double t) {
        return A.mul(t).mexp();
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public DenseMatrix getD(DenseMatrix H) {
        int d = H.cols;
        DenseMatrix h = DistancesHelper.diagToVector(H);
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
        DenseMatrix Pref = DistancesHelper.pinv(D).mmul(A);

        // W = P^{ref} (element-wise)* exp(-βC)
        DenseMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DenseMatrix W = Pref.mul(DistancesHelper.exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DenseMatrix I = eye(d);
        DenseMatrix Z = DistancesHelper.pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DenseMatrix Dh = diag(DistancesHelper.diagToVector(Z));
        DenseMatrix Zh = Z.mmul(DistancesHelper.pinv(Dh));

        // Φ = -1/β * log(Z^h)
        DenseMatrix F = DistancesHelper.log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DenseMatrix FE = F.add(F.t()).div(2);

        return FE.sub(diag(DistancesHelper.diagToVector(FE)));
    }

    public DenseMatrix sqrtD(DenseMatrix D) {
        return DistancesHelper.sqrt(D);
    }
}
