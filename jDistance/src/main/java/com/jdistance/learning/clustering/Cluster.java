package com.jdistance.learning.clustering;

import jeigen.DenseMatrix;

import java.util.List;

public class Cluster {
    List<Integer> nodes;
    int n;
    DenseMatrix h;

    Cluster(List<Integer> nodes, int length) {
        this.nodes = nodes;
        this.n = nodes.size();
        this.h = DenseMatrix.ones(length, 1).div(n);
    }
}
