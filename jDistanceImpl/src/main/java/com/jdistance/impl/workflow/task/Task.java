package com.jdistance.impl.workflow.task;

import com.jdistance.graph.Graph;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.gridsearch.MetricStatisticsDTO;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.MetricWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

public abstract class Task {
    protected GridSearch gridSearch;
    protected AbstractDistanceWrapper metricWrapper;
    protected Map<Double, Double> result = new HashMap<>();
    private Map.Entry<Double, Double> minResult;
    private Map.Entry<Double, Double> maxResult;

    public abstract String getName();

    public abstract AbstractDistanceWrapper getMetricWrapper();

    public abstract Map<Double, Double> getResult();

    public abstract Task execute();

    public  Map<Graph, Map<Double, MetricStatisticsDTO>> getMetricStatistics() {
        return gridSearch.getMetricStatistics();
    };

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

    public Double getAvgResult() {
        OptionalDouble avgOptional = result.values().stream().mapToDouble(p -> p).average();
        return avgOptional.isPresent() ? avgOptional.getAsDouble() : null;
    }
}
