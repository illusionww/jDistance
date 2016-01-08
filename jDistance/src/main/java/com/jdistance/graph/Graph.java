package com.jdistance.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;

public class Graph {
    private DenseMatrix sparseM;
    private ArrayList<NodeData> nodeData;

    public Graph(double[][] sparseM, ArrayList<NodeData> nodeData) {
        this.sparseM = new DenseMatrix(sparseM);
        this.nodeData = nodeData;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseM;
    }

    public ArrayList<NodeData> getNodeData() {
        return nodeData;
    }
}
