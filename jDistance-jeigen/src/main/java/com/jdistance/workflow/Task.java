package com.jdistance.workflow;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math.stat.descriptive.moment.StandardDeviation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

public class Task implements Serializable {
    private String lineName;
    private Double param;

    private Estimator estimator;
    private AbstractMeasureWrapper metricWrapper;
    private Scorer scorer;
    private GraphBundle graphs;

    private Double mean = null;
    private Double sigma = null;

    public Task(String lineName, Double param, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, GraphBundle graphs) {
        this.lineName = lineName;
        this.param = param;
        this.estimator = estimator;
        this.metricWrapper = metricWrapper;
        this.scorer = scorer;
        this.graphs = graphs;
    }

    public String getLineName() {
        return lineName;
    }

    public Double getParam() {
        return param;
    }

    public Pair<String, Double> getKey() {
        return new ImmutablePair<>(lineName, param);
    }

    public Pair<Double, Double> getResult() {
        return new ImmutablePair<>(mean, sigma);
    }

    public Pair<Double, Double> execute() {
        List<Double> scoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double trueParam = metricWrapper.getScale().calc(A, param);
                DenseMatrix D = metricWrapper.calc(A, trueParam);
                if (!hasNaN(D)) {
                    Map<Integer, Integer> prediction = estimator.predict(D);
                    double score = scorer.score(D, graph.getVertices(), prediction);
                    scoresByGraph.add(score);
                }
            }
            if (scoresByGraph.size() > 0.9 * graphs.getGraphs().size()) {
                OptionalDouble optionalAvg = scoresByGraph.stream().mapToDouble(d -> d).average();
                if (optionalAvg.isPresent() && optionalAvg.getAsDouble() != 0) {
                    mean = optionalAvg.getAsDouble();
                    sigma = new StandardDeviation().evaluate(scoresByGraph.stream().mapToDouble(d -> d).toArray());
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Calculation error: distance " + metricWrapper.getName() + ", gridParam " + param);
        }
        return new ImmutablePair<>(mean, sigma);
    }

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item) || Double.isInfinite(item)) {
                return true;
            }
        }
        return false;
    }
}
