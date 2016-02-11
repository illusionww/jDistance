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
        double count = 0;
        double error = 0;
        for (int i = 0; i < D.cols; ++i) {
            for (int j = i + 1; j < D.rows; ++j) {
                if (node.get(i).getLabel().equals(node.get(j).getLabel())) {
                    for (int p = 0; p < D.cols; ++p) {
                        for (int q = p + 1; q < D.rows; ++q) {
                            if (!node.get(p).getLabel().equals(node.get(q).getLabel())) {
                                count++;
                                if (D.get(i, j) > D.get(p, q)) {
                                    error++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return new CheckerTestResultDTO(count, error);
    }

    @Override
    public DiffusionChecker clone() {
        return new DiffusionChecker(graphs);
    }
}
