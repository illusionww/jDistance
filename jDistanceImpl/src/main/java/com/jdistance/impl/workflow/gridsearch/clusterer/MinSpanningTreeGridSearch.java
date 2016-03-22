package com.jdistance.impl.workflow.gridsearch.clusterer;


import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.learning.clusterer.MinSpanningTree;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;

public class MinSpanningTreeGridSearch extends GridSearch {
    private GraphBundle graphs;
    private Integer k;

    public MinSpanningTreeGridSearch(GraphBundle graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public String getName() {
        return "MinSpanningTree: k=" + k + "; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
        Integer countErrors = 0;

        final MinSpanningTree clusterer = new MinSpanningTree(D);
        final HashMap<Integer, Integer> data = clusterer.predict(k);

        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                if (data.get(i).equals(data.get(j)) != node.get(i).getLabel().equals(node.get(j).getLabel())) {
                    countErrors += 1;
                }
            }
        }

        double total = ((double) data.size() * (double) (data.size() - 1)) / 2.0;
        return 1.0 - countErrors / total;
    }

    @Override
    public MinSpanningTreeGridSearch clone() {
        return new MinSpanningTreeGridSearch(graphs, k);
    }
}
