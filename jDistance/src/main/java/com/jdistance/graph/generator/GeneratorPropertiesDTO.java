package com.jdistance.graph.generator;

import java.util.List;

public class GeneratorPropertiesDTO {
    private int graphsCount;
    private int nodesCount;
    private int clustersCount;
    private double p_in;
    private double p_out;
    private List<Integer> sizeOfClusters;
    private double[][] probabilityMatrix;

    public GeneratorPropertiesDTO(int graphsCount, int nodesCount, int clusterCount, double p_in, double p_out) {
        this.graphsCount = graphsCount;
        this.nodesCount = nodesCount;
        this.clustersCount = clusterCount;
        this.p_in = p_in;
        this.p_out = p_out;
        this.probabilityMatrix = new double[clusterCount][clusterCount];
        for (int i = 0; i < clusterCount; i++) {
            for (int j = 0; j < clusterCount; j++) {
                probabilityMatrix[i][j] = i == j ? p_in : p_out;
            }
        }
    }

    public GeneratorPropertiesDTO(int graphsCount, List<Integer> sizeOfClusters, double[][] probabilityMatrix) {
        this.graphsCount = graphsCount;
        this.nodesCount = sizeOfClusters.stream().mapToInt(Integer::intValue).sum();
        this.clustersCount = sizeOfClusters.size();
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

    public List<Integer> getSizeOfClusters() {
        return sizeOfClusters;
    }

    public double[][] getProbabilityMatrix() {
        return probabilityMatrix;
    }
}
