package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private DenseMatrix sparseMatrix;
    private List<Node> nodes;

    public Graph(double[][] sparseMatrix, List<Node> nodes) {
        this(new DenseMatrix(sparseMatrix), nodes);
    }

    public Graph(DenseMatrix sparseMatrix, List<Node> nodes) {
        this.sparseMatrix = sparseMatrix;
        this.nodes = nodes;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseMatrix;
    }

    public List<Node> getNodes() {
        return nodes;
    }
}
