package com.jdistance.impl.workflow.gridsearch.nolearning;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import jeigen.DenseMatrix;

import java.util.List;

/**
 * Пусть N - число таких пар значений расстояния (d(i,j), d(p,q)), что
 * 1) i < j и
 * 2) p < q и
 * 3) i, j лежат в одном исходном классе и
 * 4) p, q лежат в разных исходных классах.
 * Пусть n - число таких пар значений расстояния (d(i,j), d(p,q)) среди указанных выше N, что, кроме того, d(i,j) > d(p,q).
 * Показатель равен n/N.
 * Легко понять, что чем этот показатель меньше, тем метрика лучше.
 */
public class DiffusionGridSearch extends GridSearch {
    private GraphBundle graphs;

    public DiffusionGridSearch(GraphBundle graphs) {
        this.graphs = graphs;
    }

    @Override
    public String getName() {
        return "Diffusion; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
        int n = D.rows;
        long countErrors = 0;
        for (int i = 0; i < n; i++) {
            Node nodeI = node.get(i);
            for (int j = i + 1; j < n; j++) {
                Node nodeJ = node.get(j);
                for (int p = j + 1; p < n; p++) {
                    Node nodeP = node.get(p);
                    for (int q = p + 1; q < n; q++) {
                        Node nodeQ = node.get(q);
                        if (trueIfError(D, i, j, p, q, nodeI, nodeJ, nodeP, nodeQ)) {
                            countErrors++;
                        }
                        if (trueIfError(D, i, p, j, q, nodeI, nodeP, nodeJ, nodeQ)) {
                            countErrors++;
                        }
                        if (trueIfError(D, i, q, p, j, nodeI, nodeQ, nodeP, nodeJ)) {
                            countErrors++;
                        }
                    }
                }
            }
        }
        double total =  n * (n * (n * (n - 6L) + 11L) - 6L) / 8L;
        return 1.0 - countErrors / total;
    }

    private boolean trueIfError(DenseMatrix D, int a1, int a2, int b1, int b2, Node nodeA1, Node nodeA2, Node nodeB1, Node nodeB2) {
        if (nodeA1.getLabel().equals(nodeA2.getLabel()) && !nodeB1.getLabel().equals(nodeB2.getLabel())) {
            return D.get(b1, b2) < D.get(a1, a2);
        } else if (!nodeA1.getLabel().equals(nodeA2.getLabel()) && nodeB1.getLabel().equals(nodeB2.getLabel())) {
            return D.get(a1, a2) < D.get(b1, b2);
        }
        return false;
    }

    @Override
    public DiffusionGridSearch clone() {
        return new DiffusionGridSearch(graphs);
    }
}
