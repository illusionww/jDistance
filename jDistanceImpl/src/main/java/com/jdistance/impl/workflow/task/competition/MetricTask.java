package com.jdistance.impl.workflow.task.competition;

import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.MetricWrapper;
import jeigen.DenseMatrix;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetricTask extends Task {
    private DenseMatrix A;
    private Integer pointsCount;
    private Double from;
    private Double to;

    public MetricTask(MetricWrapper metricWrapper, DenseMatrix A, Integer pointsCount, Double from, Double to) {
        this.metricWrapper = metricWrapper;
        this.A = A;
        this.pointsCount = pointsCount;
        this.from = from;
        this.to = to;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + metricWrapper.getScale();
    }

    @Override
    public AbstractDistanceWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        double step = (to - from) / (pointsCount - 1);
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double i = metricWrapper.getScale().calc(A, base);
            DenseMatrix current_result = metricWrapper.calc(A, i);
            result.put(base, current_result.get(0, 1) / current_result.get(1, 2));
        });

        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }
}
