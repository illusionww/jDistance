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
    private GraphBundle graphs;
    private Integer k;

    // k - count of clusters
    public WardGridSearch(GraphBundle graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public String getName() {
        return "Ward: k=" + k + "; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
        final HashMap<Integer, Integer> predictedNodes = new Ward(D).predict(k);

        // Rate Index
        long countErrors = 0;
        for (int i = 0; i < predictedNodes.size(); ++i) {
            for (int j = i + 1; j < predictedNodes.size(); ++j) {
                if (predictedNodes.get(i).equals(predictedNodes.get(j)) != node.get(i).getLabel().equals(node.get(j).getLabel())) {
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
