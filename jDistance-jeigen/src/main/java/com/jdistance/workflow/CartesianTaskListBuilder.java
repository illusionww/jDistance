package com.jdistance.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class CartesianTaskListBuilder {
    private List<Estimator> estimators;
    private List<Scorer> scorers;
    private List<GraphBundle> graphBundles;
    private List<? extends AbstractMeasureWrapper> measures;
    private List<Double> measureParams;

    private List<Task> tasks = new ArrayList<>();

    public CartesianTaskListBuilder setEstimators(Estimator... estimators) {
        this.estimators = Arrays.asList(estimators);
        return this;
    }

    public CartesianTaskListBuilder setEstimators(List<Estimator> estimators) {
        this.estimators = estimators;
        return this;
    }

    public CartesianTaskListBuilder setScorers(Scorer... scorers) {
        this.scorers = Arrays.asList(scorers);
        return this;
    }

    public CartesianTaskListBuilder setScorers(List<Scorer> scorers) {
        this.scorers = scorers;
        return this;
    }

    public CartesianTaskListBuilder setGraphBundles(GraphBundle... graphBundles) {
        this.graphBundles = Arrays.asList(graphBundles);
        return this;
    }

    public CartesianTaskListBuilder setGraphBundles(List<GraphBundle> graphBundles) {
        this.graphBundles = graphBundles;
        return this;
    }

    public CartesianTaskListBuilder setMeasures(AbstractMeasureWrapper... measures) {
        this.measures = Arrays.asList(measures);
        return this;
    }

    public CartesianTaskListBuilder setMeasures(List<? extends AbstractMeasureWrapper> measures) {
        this.measures = measures;
        return this;
    }

    public CartesianTaskListBuilder setMeasureParams(Double... measureParams) {
        this.measureParams = Arrays.asList(measureParams);
        return this;
    }

    public CartesianTaskListBuilder setMeasureParams(List<Double> measureParams) {
        this.measureParams = measureParams;
        return this;
    }

    public CartesianTaskListBuilder linspaceMeasureParams(double from, double to, int pointsCount) {
        this.measureParams = linspace(from, to, pointsCount);
        return this;
    }

    public CartesianTaskListBuilder linspaceMeasureParams(int pointsCount) {
        this.measureParams = linspace(0, 1, pointsCount);
        return this;
    }

    public List<Task> build() {
        if (!(estimators != null && scorers != null && graphBundles != null && measures != null && measureParams != null)) {
            throw new RuntimeException("Not all params filled in CartesianTaskListBuilder");
        }
        estimators.forEach(estimator -> scorers.forEach(scorer -> graphBundles.forEach(graphBundle ->
                measures.forEach(measure -> measureParams.forEach(measureParam ->
                        tasks.add(new Task(estimator, scorer, graphBundle, measure, measureParam)))))));
        return tasks;
    }

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
