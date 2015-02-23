package com.thesis.graph;

import java.util.ArrayList;

public class Graph {
    private double[][] sparseM;
    private ArrayList<SimpleNodeData> simpleNodeData;

    public Graph(double[][] sparseM, ArrayList<SimpleNodeData> simpleNodeData) {
        this.sparseM = sparseM;
        this.simpleNodeData = simpleNodeData;
    }

    public double[][] getSparseM() {
        return sparseM;
    }

    public ArrayList<SimpleNodeData> getSimpleNodeData() {
        return simpleNodeData;
    }
}
