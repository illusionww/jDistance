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
    private Estimator estimator;
    private Scorer scorer;
    private GraphBundle graphs;
    private AbstractMeasureWrapper measure;
    private Double measureParam;

    private Double mean = null;
    private Double sigma = null;

    public Task(Estimator estimator, Scorer scorer, GraphBundle graphs, AbstractMeasureWrapper measure, Double measureParam) {
        this.estimator = estimator;
        this.measure = measure;
        this.measureParam = measureParam;
        this.scorer = scorer;
        this.graphs = graphs;
    }

    public Estimator getEstimator() {
        return estimator;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public GraphBundle getGraphs() {
        return graphs;
    }

    public AbstractMeasureWrapper getMeasure() {
        return measure;
    }

    public Double getMeasureParam() {
        return measureParam;
    }

    public Pair<Double, Double> getResult() {
        return new ImmutablePair<>(mean, sigma);
    }

    public Pair<Double, Double> execute() {
        try {
            List<Double> scoresByGraph = new ArrayList<>();
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double trueParam = measure.getScale().calc(A, measureParam);
                DenseMatrix D = measure.calc(A, trueParam);
                if (!hasNaN(D)) {
                    Map<Integer, Integer> prediction = estimator.predict(D, graphs.getProperties().getClustersCount());
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
        } catch (Throwable e) {
            System.err.println("Calculation error: distance " + measure.getName() + ", gridParam " + measureParam);
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
