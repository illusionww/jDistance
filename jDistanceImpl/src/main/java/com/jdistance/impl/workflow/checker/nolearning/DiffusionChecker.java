package com.jdistance.impl.workflow.checker.nolearning;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.CheckerTestResultDTO;
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
public class DiffusionChecker extends Checker {
    private GraphBundle graphs;

    public DiffusionChecker(GraphBundle graphs) {
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
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, List<Node> node) {
        int n = D.rows;
        long count = n * (n * (n * (n - 6) + 11) - 6) / 8;
        long error = 0;
        for (int i = 0; i < n; i++) {
            Node nodeI = node.get(i);
            for (int j = i + 1; j < n; j++) {
                Node nodeJ = node.get(j);
                for (int p = j + 1; p < n; p++) {
                    Node nodeP = node.get(p);
                    for (int q = p + 1; q < n; q++) {
                        Node nodeQ = node.get(q);
                        if (trueIfError(D, i, j, p, q, nodeI, nodeJ, nodeP, nodeQ)) {
                            error++;
                        }
                        if (trueIfError(D, i, p, j, q, nodeI, nodeP, nodeJ, nodeQ)) {
                            error++;
                        }
                        if (trueIfError(D, i, q, p, j, nodeI, nodeQ, nodeP, nodeJ)) {
                            error++;
                        }
                    }
                }
            }
        }
        return new CheckerTestResultDTO((double) count, (double) error);
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
    public DiffusionChecker clone() {
        return new DiffusionChecker(graphs);
    }
}
