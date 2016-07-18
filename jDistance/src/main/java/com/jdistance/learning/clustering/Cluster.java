package com.jdistance.learning.clustering;

import org.jblas.DoubleMatrix;

import java.util.List;

public class Cluster {
    List<Integer> nodes;
    int n;
    DoubleMatrix h;

    Cluster(List<Integer> nodes, int length) {
        this.nodes = nodes;
        this.n = nodes.size();
        this.h = DoubleMatrix.ones(length, 1).div(n);
    }
}
