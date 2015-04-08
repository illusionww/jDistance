package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.workflow.checker.Checker;
import com.thesis.workflow.checker.ClassifierChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    private Double checkerStep;
    private Map<Distance, Map<Double, Double>> result = new HashMap<>();

    public ClassifierBestParamTask(ClassifierChecker checker, Distance distance, Double from, Double to, Double step, Double checkerStep) {
        this.checker = checker;
        this.distance = distance;
        this.from = from;
        this.to = to;
        this.step = step;
        this.checkerStep = checkerStep;
    }

    @Override
    public Checker getChecker() {
        return checker;
    }

    @Override
    public Task execute() {
        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double x = from + idx * step;
            checker.setX(x);

            log.info("distance {}, x: {}", distance.getShortName(), x);
            Task task = new DefaultTask(checker, Collections.singletonList(distance), checkerStep);
            Map<Distance, Map.Entry<Double, Double>> best = task.execute().getBestResult();
            best.entrySet().stream().forEach(entry -> {
                if (!result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), new HashMap<>());
                }
                result.get(entry.getKey()).put(x, entry.getValue().getValue());
            });
        });

        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
