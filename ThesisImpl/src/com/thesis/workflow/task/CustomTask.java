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
    private Distance distance;
    private Double from;
    private Double to;
    private Double step;
    private Scale scale;
    private Map<Double, Double> result;

    public CustomTask(Checker checker, Distance distance, Double from, Double to, Double step, Scale scale) {
        this.checker = checker;
        this.distance = distance;
        this.from = from;
        this.to = to;
        this.step = step;
        this.scale = scale;
    }

    @Override
    public String getName() {
        return distance.getShortName() + " " + checker.getName();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        result = checker.seriesOfTests(distance, from, to, step, scale);
        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }
}
