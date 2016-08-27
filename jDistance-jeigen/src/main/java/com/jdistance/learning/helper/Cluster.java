package com.jdistance.learning.helper;

import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cluster {
    public List<Integer> nodes;
    public double n;
    public DenseMatrix h;

    private Map<Cluster, Double> ΔJ;

    public Cluster(List<Integer> nodes, int allNodesCount) {
        this.nodes = nodes;
        n = (double) nodes.size();

        h = DenseMatrix.zeros(allNodesCount, 1);
        double inverseN = 1.0 / n;
        for (Integer node : nodes) {
            h.set(node, 0, inverseN);
        }

        ΔJ = new HashMap<>((int) n / 2);
    }

    public double getΔJ(DenseMatrix K, Cluster Cl) {
        Double currentΔJ = ΔJ.get(Cl);
        if (currentΔJ == null) {
            return calcΔJ(K, Cl);
        } else {
            return currentΔJ;
        }
    }

    // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
    private double calcΔJ(DenseMatrix K, Cluster Cl) {
        DenseMatrix hkhl = (this.h).sub(Cl.h);
        DenseMatrix hkhlT = new DenseMatrix(1, h.rows, hkhl.getValues());
        double currentΔJ = this.n * Cl.n * hkhlT.mmul(K).mmul(hkhl).s() / (this.n + Cl.n);
        ΔJ.put(Cl, currentΔJ);
        return currentΔJ;
    }
}