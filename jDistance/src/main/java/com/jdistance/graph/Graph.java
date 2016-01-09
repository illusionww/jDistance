package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;

public class Graph {
    private DenseMatrix sparseMatrix;
    private ArrayList<NodeData> nodeData;

    public Graph(double[][] sparseMatrix, ArrayList<NodeData> nodeData) {
        this.sparseMatrix = new DenseMatrix(sparseMatrix);
        this.nodeData = nodeData;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseMatrix;
    }

    public ArrayList<NodeData> getNodeData() {
        return nodeData;
    }
}
