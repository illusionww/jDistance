package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.checker.Checker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class DefaultTask implements Task {
    private static final Logger log = LoggerFactory.getLogger(DefaultTask.class);

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
        final Set<Distance> stack = new HashSet<>();
        Stream<Distance> stream = Context.PARALLEL ? distances.parallelStream() : distances.stream();
        stream.forEach(distance -> {
            Scale scale = Scale.DEFAULT.equals(distance.getScale()) ? Context.SCALE : distance.getScale();
            stack.add(distance);
            Map<Double, Double> distanceResult = checker.clone().seriesOfTests(distance, 0.0, 1.0, step, scale);
            stack.remove(distance);
            log.info("in progress: {}", stack);
            result.put(distance, distanceResult);
        });
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
