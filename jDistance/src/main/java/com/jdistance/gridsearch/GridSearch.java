package com.jdistance.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.metric.AbstractDistanceWrapper;
import jeigen.DenseMatrix;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class GridSearch {
    private Estimator estimator;
    private List<Double> paramGrid;
    private AbstractDistanceWrapper metricWrapper;
    private GraphBundle graphs;
    private Scorer scorer;
    private boolean isParallel;
    private boolean calcMetricStatistics;

    private Map<Double, Double> scores = new ConcurrentHashMap<>();
    private Map<Graph, Map<Double, MetricStatistics>> metricStatistics;

    public GridSearch(Estimator estimator, AbstractDistanceWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount, boolean isParallel, boolean calcMetricStatistics) {
        this.estimator = estimator;
        double step = (to - from) / (pointsCount - 1);
        this.paramGrid = DoubleStream.iterate(from, i -> i + step).limit(pointsCount).boxed().collect(Collectors.toList());
        paramGrid.addAll(Arrays.asList(0.1 * step, 0.5 * step, 1.5 * step, to - 1.5 * step, to - 0.5 * step, to - 0.1 * step));
        this.metricWrapper = metricWrapper;
        this.scorer = scorer;
        this.isParallel = isParallel;
        this.calcMetricStatistics = calcMetricStatistics;
    }

    public Map<Double, Double> fit(GraphBundle graphs) {
        this.graphs = graphs;
        this.metricStatistics = graphs.getGraphs().stream().collect(Collectors.toMap(g -> g, g -> new HashMap<>()));

        Stream<Double> paramStream = isParallel ? paramGrid.parallelStream() : paramGrid.stream();
        paramStream.forEach(idx -> {
            Double score = validate(metricWrapper, idx);
            System.out.println(metricWrapper.getName() + "\t" + String.format("%1.3f", idx) + "\t" + score);
            if (score != null) {
                scores.put(idx, score);
            }
        });
        return scores;
    }

    public Map<Double, Double> getScores() {
        return scores;
    }

    public Map.Entry<Double, Double> getBestParam() {
        Optional<Map.Entry<Double, Double>> maxOptional = scores.entrySet().stream().max(Map.Entry.comparingByValue(Double::compareTo));
        return maxOptional.isPresent() ? maxOptional.get() : null;
    }

    public Map<Graph, Map<Double, MetricStatistics>> getMetricStatistics() {
        return metricStatistics;
    }

    private Double validate(AbstractDistanceWrapper metricWrapper, Double idx) {
        List<Double> validationScoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, idx);
                DenseMatrix D = metricWrapper.calc(A, parameter);
                if (!hasNaN(D)) {
                    HashMap<Integer, Integer> prediction = estimator.predict(D);
                    double score = scorer.score(D, graph.getNodes(), prediction);
                    validationScoresByGraph.add(score);
                }
                if (calcMetricStatistics) {
                    metricStatistics.get(graph).put(idx, new MetricStatistics(D, graph));
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Calculation error: distance " + metricWrapper.getName() + ", gridParam " + idx);
        }
        double avg = 0.0;
        for (double validationScore : validationScoresByGraph) {
            avg += validationScore;
        }
        return avg != 0.0 ? avg / validationScoresByGraph.size() : null;
    }

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item)) {
                return true;
            }
        }
        return false;
    }
}
