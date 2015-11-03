package com.graphgenerator.utils;

import java.util.List;

public class Input {
    private final double[][] probabilityMatrix;
    private final List<Integer> sizeOfVertices;

    public Input(List<Integer> sizeOfVertices, double[][] probabilityMatrix){
        this.sizeOfVertices = sizeOfVertices;
        this.probabilityMatrix = probabilityMatrix;
    }

    public double[][] getProbabilityMatrix() {
        return probabilityMatrix;
    }

    public List<Integer> getSizeOfVertices() {
        return sizeOfVertices;
    }


}
