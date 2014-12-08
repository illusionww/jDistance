package com.thesis.metric;

import com.thesis.algorithm.johnsons.JohnsonsAlgorithm;
import com.thesis.utils.MatrixUtils;
import com.thesis.utils.PrintUtils;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;
import org.jblas.Solve;

public class DistancesBuilder {

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
        return Solve.pinv(I.sub(A.mul(t)));
    }

    // H0 = (I + tL)^{-1}
    public static FloatMatrix getH0Forest(FloatMatrix L, float t) {
        int d = L.getColumns();
        FloatMatrix I = FloatMatrix.eye(d);
        return MatrixUtils.inverse(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public static FloatMatrix getH0Communicability(FloatMatrix A, float t) {
        return MatrixFunctions.expm(A.mul(t));
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

    // Johnson's Algorithm
    public static FloatMatrix getDShortestPath(FloatMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    public static FloatMatrix getDFreeEnergy(FloatMatrix A, float beta) {
        int d = A.getColumns();

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        FloatMatrix e = FloatMatrix.ones(d);
        FloatMatrix Pref = FloatMatrix.diag(A.mmul(e));
        PrintUtils.printArray(Pref, "Pref");

        // W = P^{ref} *(element-wise) exp (-βC)
        FloatMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        FloatMatrix W = Pref.mul(MatrixFunctions.exp(C.mul(-beta)));
        PrintUtils.printArray(W, "W");

        // Z = (I - W)^{-1}
        FloatMatrix I = FloatMatrix.eye(d);
        FloatMatrix Z = Solve.pinv(I.sub(W));
        PrintUtils.printArray(Z, "Z");

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        FloatMatrix Dh = FloatMatrix.diag(Z.diag());
        FloatMatrix Zh = Z.mul(Solve.pinv(Dh));
        PrintUtils.printArray(Zh, "Zh");

        // Φ = -1/β * log(Z^h)
        FloatMatrix F = MatrixFunctions.log(Zh).div(-beta);
        PrintUtils.printArray(F, "Φ");

        // Δ_FE = (Φ + Φ^T)/2
        return F.add(F.transpose()).div(2);
    }
}
