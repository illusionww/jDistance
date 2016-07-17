package com.jdistance.learning;

import com.jdistance.graph.Node;
import org.jblas.DoubleMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public enum Scorer {
    RI("RI") {
        @Override
        public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedClusterByNode) {
            long TPTN = 0;
            for (int i = 0; i < predictedClusterByNode.size(); ++i) {
                for (int j = i + 1; j < predictedClusterByNode.size(); ++j) {
                    if (predictedClusterByNode.get(i).equals(predictedClusterByNode.get(j)) == (nodes.get(i).getLabel() == nodes.get(j).getLabel())) {
                        TPTN += 1;
                    }
                }
            }
            return 2.0 * TPTN / (nodes.size() * (nodes.size() - 1));
        }
    },
    ARI("ARI") {
        @Override
        public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes) {
            double index = RI.score(D, nodes, predictedNodes);
            double expected = calcExpected(nodes);
            return (index - expected) / (1 - expected);
        }

        private double calcExpected(List<Node> nodes) {
            DefaultHashMap<Integer, Long> countByCluster = new DefaultHashMap<>(0L);
            for (Node node : nodes) {
                countByCluster.put(node.getLabel(), countByCluster.get(node.getLabel()) + 1);
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
            return 2.0 * (TP + TN) / (nodes.size() * (nodes.size() - 1));
        }
    },
    DIFFUSION_ORDINAL("Diff ordinal") {
        private AB_score ab_score = new AB_score() {
            @Override
            protected Pair<Double, Double> AB_addition(DoubleMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double A = 0.0, B = 0.0;
                if (nodeA1.getLabel() == nodeA2.getLabel() && nodeB1.getLabel() != nodeB2.getLabel()) {
                    A = D.get(b1, b2) > D.get(a1, a2) ? 1.0 : D.get(b1, b2) == D.get(a1, a2) ? 0.5 : 0.0;
                    B = 1.0;
                } else if (nodeA1.getLabel() != nodeA2.getLabel() && nodeB1.getLabel() == nodeB2.getLabel()) {
                    A = D.get(a1, a2) > D.get(b1, b2) ? 1.0 : D.get(a1, a2) == D.get(b1, b2) ? 0.5 : 0.0;
                    B = 1.0;
                }
                return new ImmutablePair<>(A, B);
            }
        };

        @Override
        public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    },
    DIFFUSION_CARDINAL("Diff cardinal") {
        private AB_score ab_score = new AB_score() {
            @Override
            public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes) {
                Double score = super.score(D, nodes, predictedNodes);
                return score != 0.0 ? score : 0.5;
            }

            @Override
            protected Pair<Double, Double> AB_addition(DoubleMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double h = 0.0;
                if (nodeA1.getLabel() == nodeA2.getLabel() && nodeB1.getLabel() != nodeB2.getLabel()) {
                    h = D.get(b1, b2) - D.get(a1, a2);
                } else if (nodeA1.getLabel() != nodeA2.getLabel() && nodeB1.getLabel() == nodeB2.getLabel()) {
                    h = D.get(a1, a2) - D.get(b1, b2);
                }
                return new ImmutablePair<>((Math.signum(h) + 1.0) * Math.sqrt(Math.abs(h)), 2.0 * Math.sqrt(Math.abs(h)));
            }
        };

        @Override
        public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    };

    private String name;

    Scorer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes);

    abstract class AB_score {
        public double score(DoubleMatrix D, List<Node> nodes, Map<Integer, Integer> predictedNodes) {
            int n = D.rows;
            double countErrors = 0;
            double total = 0;

            for (int i = 0; i < n; i++) {
                Node nodeI = nodes.get(i);
                for (int j = i + 1; j < n; j++) {
                    Node nodeJ = nodes.get(j);
                    for (int p = j + 1; p < n; p++) {
                        Node nodeP = nodes.get(p);
                        for (int q = p + 1; q < n; q++) {
                            Node nodeQ = nodes.get(q);
                            Pair<Double, Double> result = AB_addition(D, i, j, p, q, nodeI, nodeJ, nodeP, nodeQ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                            result = AB_addition(D, i, p, j, q, nodeI, nodeP, nodeJ, nodeQ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                            result = AB_addition(D, i, q, p, j, nodeI, nodeQ, nodeP, nodeJ);
                            countErrors += result.getLeft();
                            total += result.getRight();
                        }
                    }
                }
            }
            return 1.0 - countErrors / total;
        }

        protected abstract Pair<Double, Double> AB_addition(DoubleMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2);
    }
}
