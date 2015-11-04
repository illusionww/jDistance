package com.jdistance.impl.workflow.task;

import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetricTask extends Task {
    private Distance distance;
    private DenseMatrix A;
    private Integer pointsCount;
    private Double from;
    private Double to;

    private Map<Double, Double> results = new HashMap<>();

    public MetricTask(Distance distance, DenseMatrix A, Integer pointsCount, Double from, Double to) {
        this.distance = distance;
        this.A = A;
        this.pointsCount = pointsCount;
        this.from = from;
        this.to = to;
    }

    @Override
    public String getName() {
        return DistanceClass.getDistanceName(distance) + " " + distance.getScale();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        double step = (to - from) / (pointsCount - 1);
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double i = distance.getScale().calc(A, base);
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
