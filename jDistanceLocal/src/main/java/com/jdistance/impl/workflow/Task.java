package com.jdistance.impl.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.gridsearch.GridSearch;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.Scale;

public class Task {
    private String name;
    private GridSearch gridSearch;
    private GraphBundle graphs;

    public Task(String name, Estimator estimator, Scorer scorer, AbstractDistanceWrapper metricWrapper, GraphBundle graphs, Integer pointsCount) {
        this(name, new GridSearch(estimator, metricWrapper, scorer, 0.0, 1.0, pointsCount, Context.getInstance().isParallelGrid(), Context.getInstance().getCalcMetricStatistics()), graphs);
    }

    public Task(Estimator estimator, Scorer scorer, AbstractDistanceWrapper metricWrapper, GraphBundle graphs, Integer pointsCount) {
        this(metricWrapper.getName(), new GridSearch(estimator, metricWrapper, scorer, 0.0, 1.0, pointsCount, Context.getInstance().isParallelGrid(), Context.getInstance().getCalcMetricStatistics()), graphs);
    }

    public Task(Estimator estimator, Scorer scorer, AbstractDistanceWrapper metricWrapper, GraphBundle graphs, Double from, Double to, Integer pointsCount) {
        this(metricWrapper.getName(), new GridSearch(estimator, metricWrapper, scorer, from, to, pointsCount, Context.getInstance().isParallelGrid(), Context.getInstance().getCalcMetricStatistics()), graphs);
        metricWrapper.setScale(Scale.LINEAR);
    }

    private Task(String name, GridSearch gridSearch, GraphBundle graphs) {
        this.name = name;
        this.gridSearch = gridSearch;
        this.graphs = graphs;
    }

    public String getName() {
        return name;
    }

    public Task execute() {
        gridSearch.fit(graphs);
        return this;
    }

    public GridSearch getGridSearch() {
        return gridSearch;
    }
}
