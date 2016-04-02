package com.jdistance.impl.workflow.gridsearch.nolearning;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import jeigen.DenseMatrix;

public class StubGridSearch extends GridSearch {
    public StubGridSearch(GraphBundle graphs) {
        super(graphs);
    }

    @Override
    public String getName() {
        return "StubGridSearch; " + graphs.getName();
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D) {
        return 0;
    }

    @Override
    public GridSearch clone() {
        return new StubGridSearch(graphs);
    }
}
