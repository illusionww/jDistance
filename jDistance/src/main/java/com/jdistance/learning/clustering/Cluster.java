package com.jdistance.learning.clustering;

import org.jblas.DoubleMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    public List<Integer> nodes;
    public double n;
    public DoubleMatrix h;

    private Map<Cluster, Double> ΔJ = new HashMap<>();

    Cluster(List<Integer> nodes, int length) {
        this.nodes = nodes;
        n = nodes.size();
        h = DoubleMatrix.zeros(length, 1);
        double inverseN = 1.0 / n;
        for (Integer node : nodes) {
            h.put(node, 0, inverseN);
        }
    }

    public double getΔJ(DoubleMatrix K, Cluster Cl) {
        Double currentΔJ = ΔJ.get(Cl);
        if (currentΔJ == null) {
            currentΔJ = calcΔJ(K, Cl);
        }
        return currentΔJ;
    }

    // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
    private double calcΔJ(DoubleMatrix K, Cluster Cl) {
        double norm = this.n * Cl.n / (this.n + Cl.n);
        DoubleMatrix hkhl = (this.h).sub(Cl.h);
        DoubleMatrix hkhlT = new DoubleMatrix(1, h.rows, hkhl.toArray());
        double currentΔJ = norm * hkhlT.mmul(K).mmul(hkhl).scalar();
        ΔJ.put(Cl, currentΔJ);
        return currentΔJ;
    }
}
