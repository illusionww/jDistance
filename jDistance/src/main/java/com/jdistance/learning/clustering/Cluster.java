package com.jdistance.learning.clustering;

import org.jblas.DoubleMatrix;

import java.util.List;

public class Cluster {
    List<Integer> nodes;
    DoubleMatrix h;
    int n;

    Cluster(List<Integer> nodes, int length) {
        this.nodes = nodes;
        n = nodes.size();
        h = DoubleMatrix.zeros(length, 1);
        refreshH();
    }

    void refreshH() {
        h = DoubleMatrix.zeros(h.rows, 1);
        for (Integer node : nodes) {
            h.put(node, 0, 1 / (double) n);
        }
    }
}
