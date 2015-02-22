package com.thesis.metric;

import com.thesis.metric.algorithm.johnsons.JohnsonsAlgorithm;
import com.thesis.metric.hepler.MatrixHelper;
import org.jblas.*;

public class DistancesBuilder {
    private DistancesBuilder() {}

    // α > 0 -> 0 < t < ρ^{-1}
    public static float alphaToT(FloatMatrix A, float alpha) {
        float ro = 0;
        ComplexFloatMatrix cfm = Eigen.eigenvalues(A);
        for (ComplexFloat[] row : cfm.toArray2()) {
            ro = row[0].abs() > ro ? row[0].abs() : ro;
        }
        return (float)1.0/((float)1.0/alpha + ro);
    }


    public static FloatMatrix getL(FloatMatrix A) {
        int d = A.getRows();
        float[][] a = A.toArray2();
        float[] rowSums = new float[d];
        for (int i = 0; i < d; i++) {
            float rowSum = 0;
            for (float element : a[i]) {
                rowSum += element;
            }
            rowSums[i] = rowSum;
        }
        return FloatMatrix.diag(new FloatMatrix(rowSums)).sub(A);
    }

    // H = log(H0)
    public static FloatMatrix H0toH(FloatMatrix H0) {
        return MatrixFunctions.logi(H0);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public static FloatMatrix getD(FloatMatrix H) {
        int d = H.getColumns();
        FloatMatrix h = H.diag();
        FloatMatrix i = FloatMatrix.ones(d, 1);
        return h.mmul(i.transpose()).add(i.mmul(h.transpose())).sub(H).sub(H.transpose()).div(2);
    }

    // H = (L + J)^{-1}
    public static FloatMatrix getHResistance(FloatMatrix L) {
        int d = L.getColumns();
        float j = (float)1.0/d;
        FloatMatrix J = FloatMatrix.ones(d, d).mul(j);
        return Solve.pinv(L.add(J));
    }

    // H0 = (I - tA)^{-1}
    public static FloatMatrix getH0Walk(FloatMatrix A, float t) {
        int d = A.getColumns();
        FloatMatrix I = FloatMatrix.eye(d);
        FloatMatrix ins = I.sub(A.mul(t));
        return Solve.pinv(ins);
    }

    // H0 = (I + tL)^{-1}
    public static FloatMatrix getH0Forest(FloatMatrix L, float t) {
        int d = L.getColumns();
        FloatMatrix I = FloatMatrix.eye(d);
        return MatrixHelper.inverse(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public static FloatMatrix getH0Communicability(FloatMatrix A, float t) {
        return MatrixFunctions.expm(A.mul(t));
    }

    // Johnson's Algorithm
    public static FloatMatrix getDShortestPath(FloatMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    public static FloatMatrix getDFreeEnergy(FloatMatrix A, float beta) {
        int d = A.getColumns();

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        FloatMatrix e = FloatMatrix.ones(d);
        FloatMatrix Pref = FloatMatrix.diag(A.mmul(e));

        // W = P^{ref} *(element-wise) exp (-βC)
        FloatMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        FloatMatrix W = Pref.mul(MatrixFunctions.exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        FloatMatrix I = FloatMatrix.eye(d);
        FloatMatrix Z = Solve.pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        FloatMatrix Dh = FloatMatrix.diag(Z.diag());
        FloatMatrix Zh = Z.mul(Solve.pinv(Dh));

        // Φ = -1/β * log(Z^h)
        FloatMatrix F = MatrixFunctions.log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        return F.add(F.transpose()).div(2);
    }
}
