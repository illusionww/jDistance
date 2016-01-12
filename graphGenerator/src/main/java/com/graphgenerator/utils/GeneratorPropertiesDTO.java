package com.graphgenerator.utils;

import java.util.Collections;
import java.util.List;

public class GeneratorPropertiesDTO {
    private final List<Integer> sizeOfClusters;
    private final double[][] probabilityMatrix;

    public GeneratorPropertiesDTO() {
        this.sizeOfClusters = Collections.<Integer>emptyList();
        this.probabilityMatrix = new double[][]{};
    }

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
