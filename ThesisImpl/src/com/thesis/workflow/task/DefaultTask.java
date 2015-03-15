package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.workflow.Environment;
import com.thesis.workflow.checker.Checker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DefaultTask implements Task {
    private List<Distance> distances;
    private Checker checker;
    private Double step;
    private Map<Distance, Map<Double, Double>> result;

    public DefaultTask(Checker checker, List<Distance> distances, Double step) {
        this.distances = distances;
        this.checker = checker;
        this.step = step;
        this.result = new ConcurrentHashMap<>();
    }

    public DefaultTask(Checker checker, Distance distance, Double step) {
        this.distances = new ArrayList<>();
        distances.add(distance);
        this.checker = checker;
        this.step = step;
        this.result = new ConcurrentHashMap<>();
    }

    @Override
    public Checker getChecker() {
        return checker;
    }

    @Override
    public Task execute() {
        Stream<Distance> stream = Environment.PARALLEL ? distances.parallelStream() : distances.stream();
        stream.forEach(distance -> {
            Scale scale = Scale.DEFAULT.equals(distance.getScale()) ? Environment.SCALE : distance.getScale();
            Map<Double, Double> distanceResult = checker.clone().seriesOfTests(distance, 0.0, 1.0, step, scale);
            result.put(distance, distanceResult);
        });
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
