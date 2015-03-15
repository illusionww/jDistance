package com.thesis.workflow.task;

import com.thesis.workflow.Environment;
import com.thesis.workflow.checker.Checker;
import com.thesis.metric.Scale;
import com.thesis.metric.Distance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomTask implements Task {
    private List<Distance> distances;
    private Checker checker;
    private Double from;
    private Double to;
    private Double step;
    private Map<Distance, Map<Double, Double>> result = new HashMap<>();

    public CustomTask(Checker checker, List<Distance> distances, Double from, Double to, Double step) {
        this.checker = checker;
        this.distances = distances;
        this.from = from;
        this.to = to;
        this.step = step;
    }

    @Override
    public Checker getChecker() {
        return checker;
    }

    @Override
    public Task execute() {
        if (Environment.PARALLEL) {
            distances.parallelStream().forEach(distance -> {
                Map<Double, Double> distanceResult = checker.seriesOfTests(distance, from, to, step, Scale.LINEAR);
                result.put(distance, distanceResult);
            });
        } else {
            distances.forEach(distance -> {
                Map<Double, Double> distanceResult = checker.seriesOfTests(distance, from, to, step, Scale.LINEAR);
                result.put(distance, distanceResult);
            });
        }
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
