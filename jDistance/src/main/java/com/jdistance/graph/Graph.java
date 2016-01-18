package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;

public class Graph {
    private DenseMatrix sparseMatrix;
    private ArrayList<Node> node;

    public Graph(double[][] sparseMatrix, ArrayList<Node> node) {
        this.sparseMatrix = new DenseMatrix(sparseMatrix);
        this.node = node;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseMatrix;
    }

    public ArrayList<Node> getNode() {
        return node;
    }
}
