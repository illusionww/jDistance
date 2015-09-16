package com.jdistance.workflow.checker;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.graph.Graph;
import com.jdistance.graph.SimpleNodeData;
import jeigen.DenseMatrix;

import java.util.ArrayList;

public class DiffusionChecker extends Checker {
    private static final CheckerType type = CheckerType.CLUSTERER;

    private GraphBundle graphs;
    private Integer k;

    public DiffusionChecker(GraphBundle graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public String getName() {
        return type.name() + " (k=" + k + ")" + graphs.getName();
    }

    @Override
    public CheckerType getType() {
        return type;
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
        double count = 0;
        double error = 0;
        for (int i = 0; i < D.cols; ++i) {
            for (int j = i + 1; j < D.rows; ++j) {
                if (simpleNodeData.get(i).getLabel().equals(simpleNodeData.get(j).getLabel())) {
                    for (int p = 0; p < D.cols; ++p) {
                        for (int q = p + 1; q < D.rows; ++q) {
                            if (!simpleNodeData.get(i).getLabel().equals(simpleNodeData.get(j).getLabel())) {
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
        return new CheckerTestResultDTO(1.0d, error / count);
    }

    @Override
    public ClustererChecker clone() {
        return new ClustererChecker(graphs, k);
    }
}
