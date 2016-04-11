package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.MetricWrapper;
import com.jdistance.metric.Scale;

import java.util.Map;

public class CustomTask extends Task {
    private String name;
    private Double from;
    private Double to;
    private Integer pointsCount;

    public CustomTask(String name, GridSearch gridSearch, AbstractDistanceWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        this(gridSearch, metricWrapper, from, to, pointsCount);
        this.name = name;
    }

    public CustomTask(GridSearch gridSearch, AbstractDistanceWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        this.name = metricWrapper != null && gridSearch != null ? metricWrapper.getName() + " " + gridSearch.getName() : null;
        this.gridSearch = gridSearch;
        this.metricWrapper = metricWrapper;
        this.from = from;
        this.to = to;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AbstractDistanceWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        metricWrapper.setScale(Scale.LINEAR);
        result = gridSearch.seriesOfTests(metricWrapper, from, to, pointsCount);
        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }
}
