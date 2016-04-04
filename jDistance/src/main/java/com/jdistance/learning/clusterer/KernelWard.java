package com.jdistance.learning.clusterer;

import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class KernelWard extends Ward {
    public KernelWard(DenseMatrix K) {
        super(K);
    }

    // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
    protected double calcΔJ(Cluster Ck, Cluster Cl) {
        double norm = Ck.n * Cl.n / (double) (Ck.n + Cl.n);
        DenseMatrix hkhl = (Ck.h).sub(Cl.h);
        double currentΔJ = -hkhl.t().mmul(K).mmul(hkhl).mul(norm).s();
        ΔJ.put(new ImmutablePair<>(Ck, Cl), currentΔJ);
        return currentΔJ;
    }
}
