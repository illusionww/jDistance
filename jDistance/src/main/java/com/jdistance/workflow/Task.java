package com.jdistance.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.gridsearch.GridSearch;

import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    private GridSearch gridSearch;
    private GraphBundle graphs;

    public Task(String name, GridSearch gridSearch, GraphBundle graphs) {
        this.name = name;
        this.gridSearch = gridSearch;
        this.graphs = graphs;
    }

    public void execute() {
        gridSearch.fit(graphs);
    }

    public String getName() {
        return name;
    }

    public GridSearch getGridSearch() {
        return gridSearch;
    }
}
