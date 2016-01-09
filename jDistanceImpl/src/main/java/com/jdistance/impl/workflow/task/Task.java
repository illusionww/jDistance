package com.jdistance.impl.workflow.task;

import com.jdistance.metric.MetricWrapper;

import java.util.Map;
import java.util.Optional;

public abstract class Task {
    public abstract String getName();

    public abstract MetricWrapper getMetricWrapper();

    public abstract Map<Double, Double> getResults();

    public abstract Task execute();

    public Map.Entry<Double, Double> getBestResult() {
        Optional<Map.Entry<Double, Double>> maxOptional = getResults().entrySet().stream().max(Map.Entry.comparingByValue(Double::compareTo));
        return maxOptional.isPresent() ? maxOptional.get() : null;
    }
}
