package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;

public class Graph {
    private DenseMatrix sparseMatrix;
    private ArrayList<Node> nodes;

    public Graph(double[][] sparseMatrix, ArrayList<Node> nodes) {
        this.sparseMatrix = new DenseMatrix(sparseMatrix);
        this.nodes = nodes;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseMatrix;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }
}
