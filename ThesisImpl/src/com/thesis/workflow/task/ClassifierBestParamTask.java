package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.workflow.checker.ClassifierChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassifierBestParamTask extends Task {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    private ClassifierChecker checker;
    private Distance distance;
    private Double from;
    private Double to;
    private Double step;
    private Integer pointsCount;
    private Map<Double, Double> result = new HashMap<>();

    public ClassifierBestParamTask(ClassifierChecker checker, Distance distance, Double from, Double to, Double step, int pointsCount) {
        this.checker = checker;
        this.distance = distance;
        this.from = from;
        this.to = to;
        this.step = step;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return distance.getName() + checker.getName();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double x = from + idx * step;
            checker.setX(x);
            log.info("distance {}, x: {}", distance.getName(), x);
            Task task = new DefaultTask(checker, distance, pointsCount);
            Map.Entry<Double, Double> best = task.execute().getBestResult();
            result.put(x, best.getValue());
        });

        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }
}
