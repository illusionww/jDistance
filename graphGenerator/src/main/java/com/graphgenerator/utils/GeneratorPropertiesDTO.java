package com.graphgenerator.utils;

import java.util.List;

public class GeneratorPropertiesDTO {
    private final List<Integer> sizeOfClusters;
    private final double[][] probabilityMatrix;

    public GeneratorPropertiesDTO(List<Integer> sizeOfClusters, double[][] probabilityMatrix) {
        this.sizeOfClusters = sizeOfClusters;
        this.probabilityMatrix = probabilityMatrix;
    }

    public List<Integer> getSizeOfClusters() {
        return sizeOfClusters;
    }

    public double[][] getProbabilityMatrix() {
        return probabilityMatrix;
    }
}
