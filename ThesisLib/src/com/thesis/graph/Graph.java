package com.thesis.graph;

import jeigen.DenseMatrix;

import java.util.ArrayList;

public class Graph {
    private DenseMatrix sparseM;
    private ArrayList<SimpleNodeData> simpleNodeData;

    public Graph(double[][] sparseM, ArrayList<SimpleNodeData> simpleNodeData) {
        this.sparseM = new DenseMatrix(sparseM);
        this.simpleNodeData = simpleNodeData;
    }

    public DenseMatrix getSparseMatrix() {
        return sparseM;
    }

    public ArrayList<SimpleNodeData> getSimpleNodeData() {
        return simpleNodeData;
    }
}
