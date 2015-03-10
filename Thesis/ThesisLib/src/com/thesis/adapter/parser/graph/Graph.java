package com.thesis.adapter.parser.graph;

import org.jblas.DoubleMatrix;

import java.util.ArrayList;

public class Graph {
    private DoubleMatrix sparseM;
    private ArrayList<SimpleNodeData> simpleNodeData;

    public Graph(double[][] sparseM, ArrayList<SimpleNodeData> simpleNodeData) {
        this.sparseM = new DoubleMatrix(sparseM);
        this.simpleNodeData = simpleNodeData;
    }

    public DoubleMatrix getSparseMatrix() {
        return sparseM;
    }

    public ArrayList<SimpleNodeData> getSimpleNodeData() {
        return simpleNodeData;
    }
}
