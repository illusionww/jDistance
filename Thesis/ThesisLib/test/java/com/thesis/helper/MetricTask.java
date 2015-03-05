package com.thesis.helper;

import com.thesis.metric.Distance;
import com.thesis.workflow.checker.Checker;
import com.thesis.workflow.task.Task;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetricTask implements Task {
    private List<Distance> distances;
    private DoubleMatrix A;
    private Double step;
    private Map<Distance, Map<Double, Double>> result = new HashMap<>();

    public MetricTask(List<Distance> distances, DoubleMatrix A, Double step) {
        this.distances = distances;
        this.A = A;
        this.step = step;
    }

    public MetricTask(Distance distance, DoubleMatrix A, Double step) {
        this.distances = new ArrayList<>();
        distances.add(distance);
        this.A = A;
        this.step = step;
    }

    @Override
    public Checker getChecker() {
        return null;
    }

    @Override
    public Task execute(boolean parallel) {
        distances.forEach(distance -> {
            int countOfPoints = (int) Math.round(Math.floor(1 / step) + 1);
            Map<Double, Double> results = new HashMap<>();
            IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = idx * step;
                Double i = distance.getScale().calc(base);
                DoubleMatrix result = distance.getD(A, i);
                results.put(base, result.get(0,1)/result.get(1,2));
            });

            result.put(distance, results);
        });
        return this;
    }

    @Override
    public Map<Distance, Map<Double, Double>> getResults() {
        return result;
    }
}
