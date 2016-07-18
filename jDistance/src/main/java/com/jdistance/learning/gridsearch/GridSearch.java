package com.jdistance.learning.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.measure.AbstractMeasureWrapper;
import org.jblas.DoubleMatrix;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class GridSearch implements Serializable {
    protected String name;
    protected Estimator estimator;
    protected AbstractMeasureWrapper metricWrapper;
    protected Scorer scorer;

    protected List<Double> paramGrid;
    protected GraphBundle graphs;

    public GridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount) {
        this(name, estimator, metricWrapper, scorer, linspace(from, to, pointsCount));
    }

    public GridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, List<Double> paramGrid) {
        this.name = name;
        this.estimator = estimator;
        this.metricWrapper = metricWrapper;
        this.scorer = scorer;
        this.paramGrid = paramGrid;
    }

    private static List<Double> linspace(double from, double to, int pointsCount) {
        double step = (to - from) / (pointsCount - 1);
        List<Double> paramGrid = DoubleStream.iterate(from, i -> i + step).limit(pointsCount).boxed().collect(Collectors.toList());
        paramGrid.addAll(Arrays.asList(0.1 * step, 0.5 * step, to - 0.5 * step, to - 0.1 * step));
        Collections.sort(paramGrid);
        return paramGrid;
    }

    public abstract void fit(GraphBundle graphs);

    protected Double validate(Double idx, AbstractMeasureWrapper metricWrapper) {
        List<Double> scoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DoubleMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, idx);
                DoubleMatrix D = metricWrapper.calc(A, parameter);
                if (!hasNaN(D)) {
                    Map<Integer, Integer> prediction = estimator.predict(D);
                    double score = scorer.score(D, graph.getNodes(), prediction);
                    scoresByGraph.add(score);
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Calculation error: distance " + metricWrapper.getName() + ", gridParam " + idx);
//            throw new RuntimeException(e);
        }

        if (scoresByGraph.size() < 0.9 * graphs.getGraphs().size()) {
            return null;
        }
        double avg = avg(scoresByGraph);
        return avg != 0.0 ? avg : null;
    }

    private double avg(List<Double> scoresByGraph) {
        double sum = 0.0;
        for (double score : scoresByGraph) {
            sum += score;
        }
        return sum / scoresByGraph.size();
    }

    private boolean hasNaN(DoubleMatrix D) {
        for (double item : D.toArray()) {
            if (Double.isNaN(item)) {
                return true;
            }
        }
        return false;
    }
}
