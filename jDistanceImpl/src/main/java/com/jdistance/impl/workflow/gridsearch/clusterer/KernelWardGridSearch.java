package com.jdistance.impl.workflow.gridsearch.clusterer;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.learning.clusterer.KernelWard;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.List;

public class KernelWardGridSearch extends WardGridSearch {
    public KernelWardGridSearch(GraphBundle graphs, Integer k) {
        super(graphs, k);
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D) {
        final List<Node> nodes = graph.getNodes();
        final HashMap<Integer, Integer> predictedNodes = new KernelWard(D).predict(k);
        return rateIndex(nodes, predictedNodes);
    }
}
