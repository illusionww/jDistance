package com.thesis.workflow.task;

import com.thesis.workflow.checker.Checker;
import com.thesis.metric.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTask implements Task {
    private List<Distance> distances;
    private Checker checker;
    private Double step;
    private Map<Distance, Map<Double, Double>> result = new HashMap<>();

    public DefaultTask(Checker checker, List<Distance> distances, Double step) {
        this.distances = distances;
        this.checker = checker;
        this.step = step;
    }

    public DefaultTask(Checker checker, Distance distance, Double step) {
        this.distances = new ArrayList<>();
        distances.add(distance);
        this.checker = checker;
        this.step = step;
    }

    @Override
    public Checker getChecker() {
        return checker;
    }

    @Override
    public Task execute() {
        for (Distance distance : distances) {
            Map<Double, Double> distanceResult = checker.seriesOfTests(distance, 0.0, 1.0, step, distance.getScale());
            result.put(distance, distanceResult);
        }
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
