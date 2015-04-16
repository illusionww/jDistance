package com.thesis.helper;

import com.thesis.metric.Distance;
import com.thesis.workflow.task.Task;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetricTask extends Task {
    private Distance distance;
    private DenseMatrix A;
    private Double step;
    private Map<Double, Double> results = new HashMap<>();

    public MetricTask(Distance distance, DenseMatrix A, Double step) {
        this.distance = distance;
        this.A = A;
        this.step = step;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        int countOfPoints = (int) Math.round(Math.floor(1 / step) + 1);
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = idx * step;
            Double i = distance.getScale().calc(base);
            DenseMatrix result = distance.getD(A, i);
            results.put(base, result.get(0, 1) / result.get(1, 2));
        });

        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return results;
    }
}
