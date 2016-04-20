package com.jdistance.learning;

import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;

public enum Scorer {
    RATE_INDEX {
        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            long countErrors = 0;
            for (int i = 0; i < predictedNodes.size(); ++i) {
                for (int j = i + 1; j < predictedNodes.size(); ++j) {
                    if (predictedNodes.get(i).equals(predictedNodes.get(j)) != nodes.get(i).getLabel().equals(nodes.get(j).getLabel())) {
                        countErrors += 1;
                    }
                }
            }
            double total = (predictedNodes.size() * (predictedNodes.size() - 1)) / 2.0;
            return 1.0 - countErrors / total;
        }
    },
    DIFFUSION_ORDINAL {
        private AB_score ab_score = new AB_score() {
            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double A = 0.0, B = 0.0;
                if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    A = D.get(b1, b2) > D.get(a1, a2) ? 1.0 : D.get(b1, b2) == D.get(a1, a2) ? 0.5 : 0.0;
                    B = 1.0;
                } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    A = D.get(a1, a2) > D.get(b1, b2) ? 1.0 : D.get(a1, a2) == D.get(b1, b2) ? 0.5 : 0.0;
                    B = 1.0;
                }
                return new ImmutablePair<>(A, B);
            }
        };

        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    },
    DIFFUSION_CARDINAL {
        private AB_score ab_score = new AB_score() {
            @Override
            public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
                Double score = super.score(D, nodes, predictedNodes);
                return score != 0.0 ? score : 0.5;
            }

            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double h = 0.0;
                if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(b1, b2) - D.get(a1, a2);
                } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(a1, a2) - D.get(b1, b2);
                }
                return new ImmutablePair<>((Math.signum(h) + 1.0) * Math.sqrt(Math.abs(h)), 2.0 * Math.sqrt(Math.abs(h)));
            }
        };

        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    },
    DIFFUSION_CARDINAL_CONTROL {
        private AB_score ab_score = new AB_score() {
            @Override
            public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
                Double score = super.score(D, nodes, predictedNodes);
                return score != 0.0 ? score : 0.5;
            }

            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double h = 0.0;
                if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(b1, b2) - D.get(a1, a2);
                } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(a1, a2) - D.get(b1, b2);
                }
                return new ImmutablePair<>(Math.signum(h) + 1.0, 2.0);
            }
        };

        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    },
    DIFFUSION_CARDINAL_WITHOUT_SQRT {
        private AB_score ab_score = new AB_score() {
            @Override
            public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
                Double score = super.score(D, nodes, predictedNodes);
                return score != 0.0 ? score : 0.5;
            }

            @Override
            protected Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
                Double h = 0.0;
                if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(b1, b2) - D.get(a1, a2);
                } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
                    h = D.get(a1, a2) - D.get(b1, b2);
                }
                return new ImmutablePair<>((Math.signum(h) + 1.0) * Math.abs(h), 2.0 * Math.abs(h));
            }
        };

        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            return ab_score.score(D, nodes, predictedNodes);
        }
    };

    public abstract double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes);

    abstract class AB_score {
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
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

        protected abstract Pair<Double, Double> AB_addition(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2);
    }
}
