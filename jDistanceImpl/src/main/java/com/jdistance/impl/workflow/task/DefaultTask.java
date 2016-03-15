package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.metric.MetricWrapper;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultTask extends Task {
    private Integer pointsCount;

    public DefaultTask(Checker checker, MetricWrapper metricWrapper, Integer pointsCount) {
        this.metricWrapper = metricWrapper;
        this.checker = checker;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + metricWrapper.getScale() + "; pointsCount = " + pointsCount + ";\n" +
                "\tChecker: " + checker.getName();
    }

    @Override
    public MetricWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        Map<Double, Double> distanceResult = checker.seriesOfTests(metricWrapper, 0.0, 1.0, pointsCount);
        result = removeNaN(distanceResult);
        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }

    private Map<Double, Double> removeNaN(Map<Double, Double> distanceResult) {
        return distanceResult.entrySet().stream().filter(entry -> !Double.isNaN(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
