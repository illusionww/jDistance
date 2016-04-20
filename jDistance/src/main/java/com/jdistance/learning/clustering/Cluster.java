package com.jdistance.learning.clustering;

import jeigen.DenseMatrix;

import java.util.List;

public class Cluster {
    List<Integer> nodes;
    DenseMatrix h;
    int n;

    Cluster(List<Integer> nodes, int length) {
        this.nodes = nodes;
        n = nodes.size();
        h = DenseMatrix.zeros(length, 1);
        refreshH();
    }

    void refreshH() {
        h = DenseMatrix.zeros(h.rows, 1);
        for (Integer node : nodes) {
            h.set(node, 0, 1 / (double) n);
        }
    }
}
