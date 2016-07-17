package com.jdistance.measure;

import com.keithschwarz.johnsons.JohnsonsAlgorithm;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import static org.jblas.DoubleMatrix.*;
import static org.jblas.MatrixFunctions.exp;
import static org.jblas.MatrixFunctions.log;
import static org.jblas.MatrixFunctions.pow;
import static org.jblas.Solve.pinv;

public class Shortcuts {
    public static DoubleMatrix getL(DoubleMatrix A) {
        return diag(sumOverRows(A).transpose()).sub(A);
    }

    public static DoubleMatrix normalize(DoubleMatrix dm) {
        double deviation = new StandardDeviation().evaluate(dm.toArray());
        return dm.div(deviation);
    }

    // H = element-wise log(H0)
    static DoubleMatrix H0toH(DoubleMatrix H0) {
        DoubleMatrix H = log(H0);
        return NaNPolice(H);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public static DoubleMatrix HtoD(DoubleMatrix H) {
        int d = H.columns;
        DoubleMatrix h = diagToVector(H);
        DoubleMatrix i = ones(d, 1);
        DoubleMatrix D = h.mmul(i.transpose()).add(i.mmul(h.transpose())).sub(H).sub(H.transpose()).div(2);
        return NaNPolice(D);
    }

    // K = -1/2 HΔH
    public static DoubleMatrix DtoK(DoubleMatrix D) {
        int size = D.rows;
        DoubleMatrix H = DoubleMatrix.eye(size).sub(ones(size, size).div(size));
        DoubleMatrix K = H.mmul(D).mmul(H).mul(-0.5);
        return NaNPolice(K);
    }

    // Johnson's Algorithm
    public static DoubleMatrix getD_SP(DoubleMatrix A) {
        return JohnsonsAlgorithm.getAllShortestPaths(A);
    }

    // H = (L + J)^{-1}
    public static DoubleMatrix getH_R(DoubleMatrix A) {
        DoubleMatrix L = getL(A);
        int d = L.columns;
        double j = 1.0 / d;
        DoubleMatrix J = ones(d, d).mul(j);
        DoubleMatrix ins = L.add(J);
        return pinv(ins);
    }

    public static DoubleMatrix getH_CCT(DoubleMatrix A) {
        DoubleMatrix H_CT = pinv(getL(A));
        DoubleMatrix pinvD = pinv(diag(sumOverRows(A).transpose()));
        DoubleMatrix H = eye(A.columns).sub(ones(A.rows, A.columns).div(A.columns));
        return H_CT.add(H.mmul(pinvD).mmul(A).mmul(pinvD).mmul(H));
    }

    public static DoubleMatrix getH_CCT2(DoubleMatrix A) {
        DoubleMatrix I = eye(A.columns);
        DoubleMatrix d = sumOverRows(A).transpose();
        DoubleMatrix D05 = diag(pow(d, -0.5));
        DoubleMatrix H = eye(A.columns).sub(ones(A.rows, A.columns).div(A.columns));
        double volG = A.sum();
        DoubleMatrix M = D05.mmul(A.sub(d.mmul(d.transpose()).div(volG))).mmul(D05);
        return H.mmul(D05).mmul(M).mmul(pinv(I.sub(M))).mmul(M).mmul(D05).mmul(H);
    }

    static DoubleMatrix getD_RSP(DoubleMatrix A, double beta) {
        int d = A.columns;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DoubleMatrix e = ones(d, 1);
        DoubleMatrix D = diag(A.mmul(e));
        DoubleMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} ◦ exp(-βC); ◦ is element-wise *
        DoubleMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DoubleMatrix W = Pref.mul(exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DoubleMatrix I = eye(d);
        DoubleMatrix Z = pinv(I.sub(W));

        // S = (Z(C ◦ W)Z)÷Z; ÷ is element-wise /
        DoubleMatrix S = Z.mmul(C.mul(W)).mmul(Z).div(Z);
        // C_ = S - e(d_S)^T; d_S = diag(S)
        DoubleMatrix C_ = S.sub(e.mmul(diagToVector(S).transpose()));
        // Δ_RSP = (C_ + C_^T)/2
        DoubleMatrix Δ_RSP = C_.add(C_.transpose()).div(2);

        return Δ_RSP.sub(diag(diagToVector(Δ_RSP)));
    }

    static DoubleMatrix getD_FE(DoubleMatrix A, double beta) {
        int d = A.columns;

        // P^{ref} = D^{-1}*A, D = Diag(A*e)
        DoubleMatrix e = ones(d, 1);
        DoubleMatrix D = diag(A.mmul(e));
        DoubleMatrix Pref = pinv(D).mmul(A);

        // W = P^{ref} (element-wise)* exp(-βC)
        DoubleMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
        DoubleMatrix W = Pref.mul(exp(C.mul(-beta)));

        // Z = (I - W)^{-1}
        DoubleMatrix I = eye(d);
        DoubleMatrix Z = pinv(I.sub(W));

        // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
        DoubleMatrix Dh = diag(diagToVector(Z));
        DoubleMatrix Zh = Z.mmul(pinv(Dh));

        // Φ = -1/β * log(Z^h)
        DoubleMatrix Φ = log(Zh).div(-beta);

        // Δ_FE = (Φ + Φ^T)/2
        DoubleMatrix Δ_FE = Φ.add(Φ.transpose()).div(2);

        return Δ_FE.sub(diag(diagToVector(Δ_FE)));
    }

    public static DoubleMatrix diagToVector(DoubleMatrix A) {
        DoubleMatrix diag = new DoubleMatrix(A.rows, 1);
        double[] values = A.toArray();
        for (int i = 0; i < A.rows; i++) {
            diag.put(i, values[i * (A.columns + 1)]);
        }
        return diag;
    }

    private static DoubleMatrix NaNPolice(DoubleMatrix D) {
        for (double item : D.toArray()) {
            if (Double.isNaN(item)) {
                return ones(D.columns, D.rows).mul(Double.NaN);
            }
        }
        return D;
    }

    public static DoubleMatrix abs(ComplexDoubleMatrix x) {
        DoubleMatrix result = new DoubleMatrix(x.rows, x.columns);
        for(int i = 0; i < x.length; ++i) {
            result.put(i, x.get(i).abs());
        }
        return result;
    }

    public static DoubleMatrix sumOverRows(DoubleMatrix x) {
        DoubleMatrix result = new DoubleMatrix(1, x.columns);
        for (int c = 0; c < x.columns; ++c) {
            double sum = 0.0D;
            for (int r = 0; r < x.rows; ++r) {
                sum += x.get(r, c);
            }
            result.put(0, c, sum);
        }
        return result;
    }

    public static DoubleMatrix sumOverCols(DoubleMatrix x) {
        DoubleMatrix result = new DoubleMatrix(x.rows, 1);
        for (int r = 0; r < x.rows; ++r) {
            double sum = 0.0D;
            for (int c = 0; c < x.columns; ++c) {
                sum += x.get(r, c);
            }
            result.put(r, 0, sum);
        }
        return result;
    }
}
