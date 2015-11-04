package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.metric.Distance;

import java.util.Map;

public class CustomTask extends Task {
    private Checker checker;
    private Distance distance;
    private Double from;
    private Double to;
    private Integer pointsCount;
    private Map<Double, Double> result;

    public CustomTask(Checker checker, Distance distance, Double from, Double to, Integer pointsCount) {
        this.checker = checker;
        this.distance = distance;
        this.from = from;
        this.to = to;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return distance.getName() + " " + checker.getName();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        result = checker.seriesOfTests(distance, from, to, pointsCount);
        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }
}
