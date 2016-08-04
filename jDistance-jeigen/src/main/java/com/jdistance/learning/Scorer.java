package com.jdistance.learning;

import com.jdistance.structures.DefaultHashMap;
import com.jdistance.graph.Vertex;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public enum Scorer {
    RI("RI") {
        @Override
        public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedClusterByNode) {
            long TPTN = 0;
            for (int i = 0; i < predictedClusterByNode.size(); ++i) {
                for (int j = i + 1; j < predictedClusterByNode.size(); ++j) {
                    if (predictedClusterByNode.get(i).equals(predictedClusterByNode.get(j)) == (vertices.get(i).getLabel() == vertices.get(j).getLabel())) {
                        TPTN += 1;
                    }
                }
            }
            return 2.0 * TPTN / (vertices.size() * (vertices.size() - 1));
        }
    },
    ARI("ARI") {
        @Override
        public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes) {
            double index = RI.score(D, vertices, predictedNodes);
            double expected = calcExpected(vertices);
            return (index - expected) / (1 - expected);
        }

        private double calcExpected(List<Vertex> vertices) {
            DefaultHashMap<Integer, Long> countByCluster = new DefaultHashMap<>(0L);
            for (Vertex vertex : vertices) {
                countByCluster.put(vertex.getLabel(), countByCluster.get(vertex.getLabel()) + 1);
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
    },
    DIFFUSION_ORDINAL("Diff ordinal") {
        private AB_score ab_score = new AB_score() {
            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Vertex vertexA1, Vertex vertexA2, Vertex vertexB1, Vertex vertexB2) {
                Double A = 0.0, B = 0.0;
                if (vertexA1.getLabel() == vertexA2.getLabel() && vertexB1.getLabel() != vertexB2.getLabel()) {
                    A = D.get(b1, b2) > D.get(a1, a2) ? 1.0 : D.get(b1, b2) == D.get(a1, a2) ? 0.5 : 0.0;
                    B = 1.0;
                } else if (vertexA1.getLabel() != vertexA2.getLabel() && vertexB1.getLabel() == vertexB2.getLabel()) {
                    A = D.get(a1, a2) > D.get(b1, b2) ? 1.0 : D.get(a1, a2) == D.get(b1, b2) ? 0.5 : 0.0;
                    B = 1.0;
                }
                return new ImmutablePair<>(A, B);
            }
        };

        @Override
        public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes) {
            return ab_score.score(D, vertices, predictedNodes);
        }
    },
    DIFFUSION_CARDINAL("Diff cardinal") {
        private AB_score ab_score = new AB_score() {
            @Override
            public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes) {
                Double score = super.score(D, vertices, predictedNodes);
                return score != 0.0 ? score : 0.5;
            }

            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Vertex vertexA1, Vertex vertexA2, Vertex vertexB1, Vertex vertexB2) {
                Double h = 0.0;
                if (vertexA1.getLabel() == vertexA2.getLabel() && vertexB1.getLabel() != vertexB2.getLabel()) {
                    h = D.get(b1, b2) - D.get(a1, a2);
                } else if (vertexA1.getLabel() != vertexA2.getLabel() && vertexB1.getLabel() == vertexB2.getLabel()) {
                    h = D.get(a1, a2) - D.get(b1, b2);
                }
                return new ImmutablePair<>((Math.signum(h) + 1.0) * Math.sqrt(Math.abs(h)), 2.0 * Math.sqrt(Math.abs(h)));
            }
        };

        @Override
        public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes) {
            return ab_score.score(D, vertices, predictedNodes);
        }
    };

    private String name;

    Scorer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes);

    abstract class AB_score {
        public double score(DenseMatrix D, List<Vertex> vertices, Map<Integer, Integer> predictedNodes) {
            int n = D.rows;
            double countErrors = 0;
            double total = 0;

            for (int i = 0; i < n; i++) {
                Vertex vertexI = vertices.get(i);
                for (int j = i + 1; j < n; j++) {
                    Vertex vertexJ = vertices.get(j);
                    for (int p = j + 1; p < n; p++) {
                        Vertex vertexP = vertices.get(p);
                        for (int q = p + 1; q < n; q++) {
                            Vertex vertexQ = vertices.get(q);
                            Pair<Double, Double> result = AB_addition(D, i, j, p, q, vertexI, vertexJ, vertexP, vertexQ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                            result = AB_addition(D, i, p, j, q, vertexI, vertexP, vertexJ, vertexQ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                            result = AB_addition(D, i, q, p, j, vertexI, vertexQ, vertexP, vertexJ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                        }
                    }
                }
            }
            return 1.0 - countErrors / total;
        }

        protected abstract Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Vertex vertexA1, Vertex vertexA2, Vertex vertexB1, Vertex vertexB2);
    }
}
