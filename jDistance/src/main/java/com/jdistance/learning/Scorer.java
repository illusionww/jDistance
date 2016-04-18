package com.jdistance.learning;

import com.jdistance.graph.Node;
import jeigen.DenseMatrix;

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
    DIFFUSION {
        @Override
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
                            Double result = trueIfError(D, i, j, p, q, nodeI, nodeJ, nodeP, nodeQ);
                            if (result != null) {
                                countErrors += result;
                                total++;
                            }
                            result = trueIfError(D, i, p, j, q, nodeI, nodeP, nodeJ, nodeQ);
                            if (result != null) {
                                countErrors += result;
                                total++;
                            }
                            result = trueIfError(D, i, q, p, j, nodeI, nodeQ, nodeP, nodeJ);
                            if (result != null) {
                                countErrors += result;
                                total++;
                            }
                        }
                    }
                }
            }
            return 1.0 - countErrors / total;
        }

        private Double trueIfError(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
            if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
                return D.get(b1, b2) < D.get(a1, a2) ? 1.0 : D.get(b1, b2) == D.get(a1, a2) ? 0.5 : 0.0;
            } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
                return D.get(a1, a2) < D.get(b1, b2) ? 1.0 : D.get(a1, a2) == D.get(b1, b2) ? 0.5 : 0.0;
            }
            return null;
        }
    },
    KERNEL_ERROR {
        @Override
        public double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes) {
            double errorsCount = 0;
            for (int i = 0; i < D.rows; i++) {
                for (int j = 0; j < D.cols; j++) {
                    if (D.get(i,j) > D.get(i, i)) {
                        errorsCount++;
                    }
                }
            }
            double total = D.rows * (D.rows - 1);
            return errorsCount/total;
        }
    };

    public abstract double score(DenseMatrix D, List<Node> nodes, HashMap<Integer, Integer> predictedNodes);
}
