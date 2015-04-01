package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.workflow.Context;
import com.thesis.workflow.checker.Checker;
import com.thesis.metric.Scale;
import com.thesis.metric.Distances;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CustomTask extends Task {
    private Checker checker;
    private List<Distance> distances;
    private Double from;
    private Double to;
    private Double step;
    private Scale scale;
    private Map<Distance, Map<Double, Double>> result = new HashMap<>();

    public CustomTask(Checker checker, List<Distance> distances, Double from, Double to, Double step, Scale scale) {
        this.checker = checker;
        this.distances = distances;
        this.from = from;
        this.to = to;
        this.step = step;
        this.scale = scale;
    }

    @Override
    public Checker getChecker() {
        return checker;
    }

    @Override
    public Task execute() {
        Stream<Distance> stream = Context.getInstance().PARALLEL ? distances.parallelStream() : distances.stream();
        stream.forEach(distance -> {
            Map<Double, Double> distanceResult = checker.seriesOfTests(distance, from, to, step, scale);
            result.put(distance, distanceResult);
        });
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
