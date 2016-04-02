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
    private Integer k;

    public MinSpanningTreeGridSearch(GraphBundle graphs, Integer k) {
        super(graphs);
        this.k = k;
    }

    @Override
    public String getName() {
        return "MinSpanningTree: k=" + k + "; " + graphs.getName();
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D) {
        final List<Node> nodes = graph.getNodes();
        Integer countErrors = 0;

        final MinSpanningTree clusterer = new MinSpanningTree(D);
        final HashMap<Integer, Integer> data = clusterer.predict(k);

        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                if (data.get(i).equals(data.get(j)) != nodes.get(i).getLabel().equals(nodes.get(j).getLabel())) {
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
