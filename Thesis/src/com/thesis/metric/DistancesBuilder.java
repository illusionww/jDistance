package com.thesis.metric;

import com.thesis.matrix.CustomUtils;
import com.thesis.matrix.ElementWise;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class DistancesBuilder {

    // H0 = (I - tA)^(-1)
    public static RealMatrix getH0Walk(RealMatrix A, double t) {
        int dimension = A.getColumnDimension();
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(dimension);
        return MatrixUtils.inverse(I.subtract(A.scalarMultiply(t)));
    }

    // H0 = (I + tL)^(-1)
    public static RealMatrix getH0Forest(RealMatrix L, double t) {
        int dimension = L.getColumnDimension();
        RealMatrix I = MatrixUtils.createRealIdentityMatrix(dimension);
        return MatrixUtils.inverse(I.add(L.scalarMultiply(t)));
    }

    // H0 = exp(tA)
    public static RealMatrix getH0Communicability(RealMatrix A, double t) {
        return ElementWise.exp(A.scalarMultiply(t));
    }

    // H = log(H0)
    public static RealMatrix H0toH(RealMatrix H0) {
        return ElementWise.ln(H0);
    }

    // D = (h*1^(-1) + 1*h^(-1) - H - H^T)/2
    public static RealMatrix getD(RealMatrix H) {
        int dimension = H.getColumnDimension();
        RealMatrix h = CustomUtils.vectorToMatrix(CustomUtils.getMainDiagonal(H));
        RealMatrix i = CustomUtils.vectorToMatrix(new ArrayRealVector(dimension, 1.0));
        return h.multiply(i.transpose()).subtract(i.multiply(h.transpose()))
                .subtract(H).subtract(H.transpose()).scalarMultiply(0.5);
    }
}
