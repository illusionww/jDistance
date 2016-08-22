package com.jdistance.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class AbstractGridSearch implements Serializable {
    protected String name;
    protected List<Task> tasks;

    public AbstractGridSearch() {
        this.tasks = new ArrayList<>();
    }

    public AbstractGridSearch(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public AbstractGridSearch addLine(String lineName, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, GraphBundle graphs, int pointsCount) {
        List<Task> lineTasks = linspace(0.0, 1.0, pointsCount).stream()
                .map(param -> new Task(lineName, param, estimator, metricWrapper, scorer, graphs))
                .collect(Collectors.toList());
        tasks.addAll(lineTasks);
        return this;
    }

    public AbstractGridSearch addLinesForDifferentMeasures(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        metricWrappers.forEach(metricWrapper -> addLine(metricWrapper.getName(), estimator, metricWrapper, scorer, graphs, pointsCount));
        if (name == null) {
            name = estimator.getName() + " - " + graphs.getName();
        }
        return this;
    }

    protected abstract AbstractGridSearchResult execute();

    private List<Double> linspace(double from, double to, int pointsCount) {
        if (pointsCount < 7) {
            throw new RuntimeException("Should be at least 7 points");
        }
        pointsCount -= 4;
        double step = (to - from) / (pointsCount - 1);
        List<Double> paramGrid = DoubleStream.iterate(from, i -> i + step)
                .limit(pointsCount)
                .boxed()
                .collect(Collectors.toList());
        paramGrid.addAll(Arrays.asList(0.1 * step, 0.5 * step, to - 0.5 * step, to - 0.1 * step));
        Collections.sort(paramGrid);
        return paramGrid;
    }
}
