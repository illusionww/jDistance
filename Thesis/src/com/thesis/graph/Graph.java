package com.thesis.graph;

import java.util.ArrayList;

public class Graph {
    private float[][] sparseM;
    private ArrayList<SimpleNodeData> simpleNodeData;

    public Graph(float[][] sparseM, ArrayList<SimpleNodeData> simpleNodeData) {
        this.sparseM = sparseM;
        this.simpleNodeData = simpleNodeData;
    }

    public float[][] getSparseM() {
        return sparseM;
    }

    public ArrayList<SimpleNodeData> getSimpleNodeData() {
        return simpleNodeData;
    }
}
