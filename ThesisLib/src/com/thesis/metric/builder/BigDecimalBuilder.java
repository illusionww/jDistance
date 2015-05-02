package com.thesis.metric.builder;

import com.thesis.utils.BigDecimalMatrix;

public class BigDecimalBuilder {
    // H0 = exp(tA)
    public BigDecimalMatrix getH0Communicability(BigDecimalMatrix A, double t) {
        return A.mul(t).mexp(100);
    }

    // H = log(H0)
    public BigDecimalMatrix H0toH(BigDecimalMatrix H0) {
        return BigDecimalHelper.log(H0);
    }

    // D = (h*1^{T} + 1*h^{T} - H - H^T)/2
    public BigDecimalMatrix getD(BigDecimalMatrix H) {
        int d = H.cols;
        BigDecimalMatrix h = BigDecimalHelper.diagToVector(H);
        BigDecimalMatrix i = BigDecimalMatrix.ones(d, 1);
        return h.mmul(i.t()).add(i.mmul(h.t())).sub(H).sub(H.t()).div(2);
    }

    public BigDecimalMatrix sqrtD(BigDecimalMatrix D) {
        return D.sqrt();
    }

}
