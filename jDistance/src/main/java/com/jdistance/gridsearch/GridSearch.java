package com.jdistance.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.gridsearch.statistics.BasicMeasureStatistics;
import com.jdistance.gridsearch.statistics.ClustersMeasureStatistics;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.distance.AbstractMeasureWrapper;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class GridSearch {
    private String name;
    private Estimator estimator;
    private List<Double> paramGrid;
    private AbstractMeasureWrapper metricWrapper;
    private GraphBundle graphs;
    private Scorer scorer;
    private boolean isParallel;
    private boolean calcMetricStatistics;

    private Map<Double, Double> scores = new ConcurrentHashMap<>();
    private Map<Double, ClustersMeasureStatistics> metricStatistics = new ConcurrentHashMap<>();

    public GridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount, boolean isParallel, boolean calcMetricStatistics) {
        this.name = name;
        this.estimator = estimator;
        double step = (to - from) / (pointsCount - 1);
        this.paramGrid = DoubleStream.iterate(from, i -> i + step).limit(pointsCount).boxed().collect(Collectors.toList());
        paramGrid.addAll(Arrays.asList(0.1 * step, 0.5 * step, to - 0.5 * step, to - 0.1 * step));
        Collections.sort(paramGrid);
        this.metricWrapper = metricWrapper;
        this.scorer = scorer;
        this.isParallel = isParallel;
        this.calcMetricStatistics = calcMetricStatistics;
    }

    public Map<Double, Double> fit(GraphBundle graphs) {
        this.graphs = graphs;

        Stream<Double> paramStream = isParallel ? paramGrid.parallelStream() : paramGrid.stream();
        paramStream.forEach(idx -> {
            Double score = validate(metricWrapper, idx);
            System.out.println(name + "\t" + String.format("%1.3f", idx) + "\t" + score);
            if (score != null) {
                scores.put(idx, score);
            }
        });
        return scores;
    }

    public Map<Double, Double> getScores() {
        return scores;
    }

    public Map<Double, ClustersMeasureStatistics> getMetricStatistics() {
        return metricStatistics;
    }

    private Double validate(AbstractMeasureWrapper metricWrapper, Double idx) {
        List<Double> scoresByGraph = new ArrayList<>();
        List<BasicMeasureStatistics> metricStatisticsByGraph = new ArrayList<>();
        List<Map<Pair<String, String>, BasicMeasureStatistics>> clustersStatisticsByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, idx);
                DenseMatrix D = metricWrapper.calc(A, parameter);
                if (!hasNaN(D)) {
                    Map<Integer, Integer> prediction = estimator.predict(D);
                    double score = scorer.score(D, graph.getNodes(), prediction);
                    scoresByGraph.add(score);
                    if (calcMetricStatistics) {
                        metricStatisticsByGraph.add(BasicMeasureStatistics.calcMinMaxAvgOfMatrix(D));
//                        clustersStatisticsByGraph.add(ClustersMeasureStatistics.calcClusterStatisticsForGraph(D, graph));
                    }
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Calculation error: distance " + metricWrapper.getName() + ", gridParam " + idx);
        }
        if (calcMetricStatistics) {
            metricStatistics.put(idx, new ClustersMeasureStatistics(ClustersMeasureStatistics.join(metricStatisticsByGraph), clustersStatisticsByGraph));
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

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item)) {
                return true;
            }
        }
        return false;
    }
}
