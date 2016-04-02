package com.jdistance.impl.workflow.gridsearch.clusterer;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.learning.clusterer.Ward;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;

public class WardGridSearch extends GridSearch {
    private Integer k;

    // k - count of clusters
    public WardGridSearch(GraphBundle graphs, Integer k) {
        super(graphs);
        this.k = k;
    }

    @Override
    public String getName() {
        return "Ward: k=" + k + "; " + graphs.getName();
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D) {
        final List<Node> nodes = graph.getNodes();
        final HashMap<Integer, Integer> predictedNodes = new Ward(D).predict(k);

        // Rate Index
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

    @Override
    public WardGridSearch clone() {
        return new WardGridSearch(graphs, k);
    }
}
