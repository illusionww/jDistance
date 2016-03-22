package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.List;

public class Graph {
    private DenseMatrix A;
    private List<Node> nodes;

    public Graph(List<Node> nodes, double[][] A) {
        this(nodes, new DenseMatrix(A));
    }

    public Graph(List<Node> nodes, DenseMatrix A) {
        this.nodes = nodes;
        this.A = A;
    }

    public DenseMatrix getA() {
        return A;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
