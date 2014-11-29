package com.thesis.metric;

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
        return Solve.pinv(I.add(L.mul(t)));
    }

    // H0 = exp(tA)
    public static FloatMatrix getH0Communicability(FloatMatrix A, float t) {
        return MatrixFunctions.expm(A.mul(t));
    }

    // H = log(H0)
    public static FloatMatrix H0toH(FloatMatrix H0) {
        return MatrixFunctions.log(H0);
    }

    // D = (h*1^{-1} + 1*h^{-1} - H - H^T)/2
    public static FloatMatrix getD(FloatMatrix H) {
        int d = H.getColumns();
        FloatMatrix h = H.diag();
        FloatMatrix i = FloatMatrix.ones(d, 1);
        return h.mmul(i.transpose()).add(i.mmul(h.transpose())).sub(H).sub(H.transpose()).div(2);
    }
}
