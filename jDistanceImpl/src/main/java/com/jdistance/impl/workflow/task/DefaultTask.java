package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.MetricWrapper;

import java.util.Map;

public class DefaultTask extends Task {
    private Integer pointsCount;

    public DefaultTask(GridSearch gridSearch, AbstractDistanceWrapper metricWrapper, Integer pointsCount) {
        this.gridSearch = gridSearch;
        this.metricWrapper = metricWrapper;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + metricWrapper.getScale() + "; pointsCount = " + pointsCount + ";\n" +
                "GridSearch: " + gridSearch.getName();
    }

    @Override
    public AbstractDistanceWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        result = gridSearch.seriesOfTests(metricWrapper, 0.0, 1.0, pointsCount);
        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }
}
