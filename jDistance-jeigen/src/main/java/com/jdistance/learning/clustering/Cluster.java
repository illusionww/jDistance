package com.jdistance.learning.clustering;

import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    public List<Integer> nodes;
    public Double n;
    public DenseMatrix h;

    private Map<Cluster, Double> ΔJ;

    Cluster(List<Integer> nodes, int allNodesCount) {
        this.nodes = nodes;
        n = (double) nodes.size();

        h = DenseMatrix.zeros(allNodesCount, 1);
        double inverseN = 1.0 / n;
        for (Integer node : nodes) {
            h.set(node, 0, inverseN);
        }

        ΔJ = new HashMap<>(n.intValue() /2);
    }

    public Double getΔJ(DenseMatrix K, Cluster Cl) {
        Double currentΔJ = ΔJ.get(Cl);
        if (currentΔJ == null) {
            currentΔJ = calcΔJ(K, Cl);
        }
        return currentΔJ;
    }

    // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
    private Double calcΔJ(DenseMatrix K, Cluster Cl) {
        Double norm = this.n * Cl.n / (this.n + Cl.n);
        DenseMatrix hkhl = (this.h).sub(Cl.h);
        DenseMatrix hkhlT = new DenseMatrix(1, h.rows, hkhl.getValues());
        Double currentΔJ = norm * hkhlT.mmul(K).mmul(hkhl).s();
        ΔJ.put(Cl, currentΔJ);
        return currentΔJ;
    }
}
