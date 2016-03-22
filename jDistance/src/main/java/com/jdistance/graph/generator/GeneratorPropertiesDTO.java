package com.jdistance.graph.generator;

import java.util.stream.IntStream;

public class GeneratorPropertiesDTO {
    private int graphsCount;
    private int nodesCount;
    private int clustersCount;
    private double p_in;
    private double p_out;
    private int[] sizeOfClusters;
    private double[][] probabilityMatrix;

    public GeneratorPropertiesDTO(int graphsCount, int nodesCount, int clustersCount, double p_in, double p_out) {
        this.graphsCount = graphsCount;
        this.nodesCount = nodesCount;
        this.clustersCount = clustersCount;
        this.p_in = p_in;
        this.p_out = p_out;
        this.sizeOfClusters = IntStream.iterate(nodesCount / clustersCount, i -> i).limit(clustersCount).toArray();
        this.probabilityMatrix = new double[clustersCount][clustersCount];
        for (int i = 0; i < clustersCount; i++) {
            for (int j = 0; j < clustersCount; j++) {
                probabilityMatrix[i][j] = i == j ? p_in : p_out;
            }
        }
    }

    public GeneratorPropertiesDTO(int graphsCount, int[] sizeOfClusters, double[][] probabilityMatrix) {
        this.graphsCount = graphsCount;
        this.nodesCount = IntStream.of(sizeOfClusters).sum();
        this.clustersCount = sizeOfClusters.length;
        this.p_in = Double.NaN;
        this.p_out = Double.NaN;
        this.sizeOfClusters = sizeOfClusters;
        this.probabilityMatrix = probabilityMatrix;
    }

    public int getGraphsCount() {
        return graphsCount;
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public int getClustersCount() {
        return clustersCount;
    }

    public double getP_in() {
        return p_in;
    }

    public double getP_out() {
        return p_out;
    }

    int[] getSizeOfClusters() {
        return sizeOfClusters;
    }

    double[][] getProbabilityMatrix() {
        return probabilityMatrix;
    }
}
