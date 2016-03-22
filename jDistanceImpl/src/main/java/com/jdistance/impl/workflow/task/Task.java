package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.metric.MetricWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Task {
    protected GridSearch gridSearch;
    protected MetricWrapper metricWrapper;
    protected Map<Double, Double> result = new HashMap<>();
    private Map.Entry<Double, Double> minResult;
    private Map.Entry<Double, Double> maxResult;

    public abstract String getName();

    public abstract MetricWrapper getMetricWrapper();

    public abstract Map<Double, Double> getResult();

    public abstract Task execute();

    public Map.Entry<Double, Double> getMinResult() {
        if (minResult == null) {
            Optional<Map.Entry<Double, Double>> maxOptional = result.entrySet().stream().min(Map.Entry.comparingByValue(Double::compareTo));
            minResult = maxOptional.isPresent() ? maxOptional.get() : null;
        }
        return minResult;
    }

    public Map.Entry<Double, Double> getMaxResult() {
        if (maxResult == null) {
            Optional<Map.Entry<Double, Double>> maxOptional = result.entrySet().stream().max(Map.Entry.comparingByValue(Double::compareTo));
            maxResult = maxOptional.isPresent() ? maxOptional.get() : null;
        }
        return maxResult;
    }
}
