package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Scorer {
    RI("RI") {
        @Override
        public double score(DenseMatrix D, List<Integer> vertices, Map<Integer, Integer> predictedClusterByNode) {
            long TPTN = 0;
            for (int i = 0; i < predictedClusterByNode.size(); ++i) {
                for (int j = i + 1; j < predictedClusterByNode.size(); ++j) {
                    if (predictedClusterByNode.get(i).equals(predictedClusterByNode.get(j)) == (Objects.equals(vertices.get(i), vertices.get(j)))) {
                        TPTN += 1;
                    }
                }
            }
            return 2.0 * TPTN / (vertices.size() * (vertices.size() - 1));
        }
    },
    ARI("ARI") {
        @Override
        public double score(DenseMatrix D, List<Integer> vertices, Map<Integer, Integer> predictedNodes) {
            double index = RI.score(D, vertices, predictedNodes);
            double expected = calcExpected(vertices);
            return (index - expected) / (1 - expected);
        }

        private double calcExpected(List<Integer> vertices) {
            HashMap<Integer, Long> countByCluster = new HashMap<>();
            for (Integer vertex : vertices) {
                countByCluster.put(vertex, countByCluster.getOrDefault(vertex, 0L) + 1);
            }
            double clustersCount = countByCluster.size();
            double TP = countByCluster.values().stream().mapToDouble(i -> i * (i - 1)).sum() / (2.0 * clustersCount);
            double sumAmongClusters = countByCluster.values().stream().mapToDouble(i -> i).sum();
            double TN = 0;
            for (Long count : countByCluster.values()) {
                sumAmongClusters -= count;
                TN += count * sumAmongClusters;
            }
            TN /= clustersCount * clustersCount;
            return 2.0 * (TP + TN) / (vertices.size() * (vertices.size() - 1));
        }
    };

    private String name;

    Scorer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double score(DenseMatrix D, List<Integer> vertices, Map<Integer, Integer> predictedNodes);
}
