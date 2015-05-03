package com.thesis.metric.builder;

import com.thesis.utils.BDMatrix;
import com.thesis.utils.MatrixAdapter;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public class BDBuilder {
    // H0 = exp(tA)
    public BDMatrix getH0Communicability(BDMatrix A, double t) {
        return A.mul(t).mexp(100);
    }

    // H = log(H0)
    public BDMatrix H0toH(BDMatrix H0) {
        return log(H0);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public BDMatrix getD(BDMatrix H) {
        int d = H.cols;
        BDMatrix h = diagToVector(H);
        BDMatrix i = BDMatrix.ones(d, 1);
        return h.mmul(i.t()).add(i.mmul(h.t())).sub(H).sub(H.t()).div(2);
    }

    public BDMatrix sqrtD(BDMatrix D) {
        return D.sqrt();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // HELPER METHODS
    //////////////////////////////////////////////////////////////////////////////////////

    public static BDMatrix log(BDMatrix A) {
        return elementWise(A, BigDecimalMath::log);
    }

    public static BDMatrix exp(BDMatrix A) {
        return elementWise(A, BigDecimalMath::exp);
    }

    private static BDMatrix elementWise(BDMatrix A, UnaryOperator<BigDecimal> operator) {
        BigDecimal[][] values = MatrixAdapter.toArray2(A);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = operator.apply(values[i][j]);
            }
        }
        return new BDMatrix(values);
    }

    public static BDMatrix diagToVector(BDMatrix A) {
        BDMatrix diag = new BDMatrix(A.rows, 1);
        BigDecimal[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    public static BDMatrix normalization(BDMatrix dm) {
        BigDecimal avg = dm.sum().sum().s().divide(new BigDecimal(dm.cols * (dm.cols - 1)));
        return dm.div(avg);
    }

}
