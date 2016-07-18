package com.jdistance.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.gridsearch.AbstractGridSearch;

import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    private AbstractGridSearch gridSearch;
    private GraphBundle graphs;

    public Task(String name, AbstractGridSearch gridSearch, GraphBundle graphs) {
        this.name = name;
        this.gridSearch = gridSearch;
        this.graphs = graphs;
    }

    public void execute() {
        gridSearch.predict(graphs);
    }

    public String getName() {
        return name;
    }

    public AbstractGridSearch getGridSearch() {
        return gridSearch;
    }
}
