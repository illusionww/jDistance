package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.metric.Distances;
import com.thesis.workflow.checker.Checker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Task {
    public abstract Checker getChecker();

    public abstract Task execute();

    public abstract Map<Distance, Map<Double, Double>> getResults();

    public Map<Distance, Map.Entry<Double, Double>> getBestResult() {
        Map<Distance, Map.Entry<Double, Double>> bestResult = new HashMap<>();
        getResults().entrySet().forEach(entry -> {
            Optional<Map.Entry<Double, Double>> maxOptional = entry.getValue().entrySet().stream().max(Map.Entry.comparingByValue(Double::compareTo));
            Map.Entry<Double, Double> max = maxOptional.isPresent() ? maxOptional.get() : null;
            bestResult.put(entry.getKey(), max);
        });
        return bestResult;
    }
}
