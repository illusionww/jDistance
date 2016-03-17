package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.metric.MetricWrapper;

import java.util.Map;

public class CustomTask extends Task {
    private Double from;
    private Double to;
    private Integer pointsCount;

    public CustomTask(Checker checker, MetricWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        this.checker = checker;
        this.metricWrapper = metricWrapper;
        this.from = from;
        this.to = to;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + checker.getName();
    }

    @Override
    public MetricWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        result = checker.seriesOfTests(metricWrapper, from, to, pointsCount);
        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }
}
