package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.metric.MetricWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultTask extends Task {
    private static final Logger log = LoggerFactory.getLogger(DefaultTask.class);

    private MetricWrapper metricWrapper;
    private Checker checker;
    private Integer pointsCount;
    private Map<Double, Double> result;

    public DefaultTask(Checker checker, MetricWrapper metricWrapper, Integer pointsCount) {
        this.metricWrapper = metricWrapper;
        this.checker = checker;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + checker.getName() + ", pointsCount=" + pointsCount + " " + metricWrapper.getMetric().getScale();
    }

    @Override
    public MetricWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        Map<Double, Double> distanceResult = checker.seriesOfTests(metricWrapper, 0.00001, 0.99999, pointsCount);
        result = removeNaN(distanceResult);
        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }

    private Map<Double, Double> removeNaN(Map<Double, Double> distanceResult) {
        return distanceResult.entrySet().stream().filter(entry -> !Double.isNaN(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
