package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.metric.MetricWrapper;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultTask extends Task {
    private Integer pointsCount;

    public DefaultTask(GridSearch gridSearch, MetricWrapper metricWrapper, Integer pointsCount) {
        this.metricWrapper = metricWrapper;
        this.gridSearch = gridSearch;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + metricWrapper.getScale() + "; pointsCount = " + pointsCount + ";\n" +
                "\tGridSearch: " + gridSearch.getName();
    }

    @Override
    public MetricWrapper getMetricWrapper() {
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
