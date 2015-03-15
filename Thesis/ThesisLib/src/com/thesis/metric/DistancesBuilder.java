package com.thesis.metric;

import com.thesis.metric.algorithm.johnsons.JohnsonsAlgorithm;
import org.jblas.*;

public class DistancesBuilder {
    // α > 0 -> 0 < t < ρ^{-1}
    public double alphaToT(DoubleMatrix A, double alpha) {
        double ro = 0;
        ComplexDoubleMatrix cfm = Eigen.eigenvalues(A);
        for (ComplexDouble[] row : cfm.toArray2()) {
            ro = row[0].abs() > ro ? row[0].abs() : ro;
        }
        return 1.0 / (1.0 / alpha + ro);
    }

    public DoubleMatrix getL(DoubleMatrix A) {
        int d = A.getRows();
        double[][] a = A.toArray2();
        double[] rowSums = new double[d];
        for (int i = 0; i < d; i++) {
            double rowSum = 0;
            for (double element : a[i]) {
                rowSum += element;
            }
            rowSums[i] = rowSum;
        }
        return DoubleMatrix.diag(new DoubleMatrix(rowSums)).sub(A);
    }

    // H = log(H0)
    public DoubleMatrix H0toH(DoubleMatrix H0) {
        return MatrixFunctions.logi(H0);
    }

    // H = (L + J)^{-1}
    public DoubleMatrix getHResistance(DoubleMatrix L) {
        int d = L.getColumns();
        double j = 1.0 / d;
        DoubleMatrix J = DoubleMatrix.ones(d, d).mul(j);
        DoubleMatrix H =  DistancesHelper.pinv(L.add(J));
        return H.mul(2); // normalization
    }

    // H0 = (I - tA)^{-1}
    public DoubleMatrix getH0Walk(DoubleMatrix A, double t) {
        int d = A.getColumns();
        DoubleMatrix I = DoubleMatrix.eye(d);
        DoubleMatrix ins = I.sub(A.mul(t));
        return DistancesHelper.pinv(ins);
    }

    // H0 = (I + tL)^{-1}
    public DoubleMatrix getH0Forest(DoubleMatrix L, double t) {
        int d = L.getColumns();
        DoubleMatrix I = DoubleMatrix.eye(d);
        return DistancesHelper.pinv(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public DoubleMatrix getH0Communicability(DoubleMatrix A, double t) {
        return MatrixFunctions.expm(A.mul(t));
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public DoubleMatrix getD(DoubleMatrix H) {
        int d = H.getColumns();
        DoubleMatrix h = H.diag();
        DoubleMatrix i = DoubleMatrix.ones(d, 1);
        return h.mmul(i.transpose()).add(i.mmul(h.transpose())).sub(H).sub(H.transpose()).div(2);
    }

    // Johnson's Algorithm
    public DoubleMatrix getDShortestPath(DoubleMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    public DoubleMatrix getDFreeEnergy(DoubleMatrix A, double beta) {
        int d = A.getColumns();

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DoubleMatrix e = DoubleMatrix.ones(d);
        DoubleMatrix D = DoubleMatrix.diag(A.mmul(e));
        DoubleMatrix Pref = DistancesHelper.pinv(D).mmul(A);

        // W = P^{ref} (element-wise)* exp(-βC)
        DoubleMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DoubleMatrix W = Pref.mul(MatrixFunctions.exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DoubleMatrix I = DoubleMatrix.eye(d);
        DoubleMatrix Z = DistancesHelper.pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DoubleMatrix Dh = DoubleMatrix.diag(Z.diag());
        DoubleMatrix Zh = Z.mmul(DistancesHelper.pinv(Dh));

        // Φ = -1/β * log(Z^h)
        DoubleMatrix F = MatrixFunctions.log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DoubleMatrix FE = F.add(F.transpose()).div(2);

        return FE.sub(DoubleMatrix.diag(FE.diag()));
    }

    public DoubleMatrix sqrtD(DoubleMatrix D) {
        return MatrixFunctions.sqrt(D);
    }
}
