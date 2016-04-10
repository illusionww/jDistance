package com.jdistance.impl.workflow.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.utils.Cloneable;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.jdistance.impl.workflow.context.ContextProvider.getContext;

public abstract class GridSearch implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(GridSearch.class);
    private Map<Graph, Map<Double, MetricStatisticsDTO>> metricStatistics;
    protected GraphBundle graphs;

    public GridSearch(GraphBundle graphs) {
        this.graphs = graphs;
        metricStatistics = graphs.getGraphs().stream().collect(Collectors.toMap(g -> g, g -> new HashMap<>()));
    }

    public abstract String getName();

    public Map<Double, Double> seriesOfTests(final AbstractDistanceWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        Date start = new Date();
        log.debug("START {}", metricWrapper.getName());

        double step = (to - from) / (pointsCount - 1);
        List<Double> grid = DoubleStream.iterate(0, i -> i+1).limit(pointsCount).boxed().collect(Collectors.toList());
        grid.add(0.1);
        grid.add(0.5);
        grid.add(1.5);
        grid.add(pointsCount - 2.5);
        grid.add(pointsCount - 1.5);
        grid.add(pointsCount - 1.1);
        Stream<Double> stream = getContext().getParallelGrid() ? grid.parallelStream() : grid.stream();

        final Map<Double, Double> validationScores = new ConcurrentHashMap<>();
        stream.forEach(idx -> {
            Double base = from + idx * step;
            Double validationScore = validate(metricWrapper, base);
            log.info("{}: {} {}", metricWrapper.getName(), base, validationScore);
            if (validationScore != null) {
                validationScores.put(base, validationScore);
            }
        });

        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.debug("END {}; time: {} ms", metricWrapper.getName(), diff);

        return validationScores;
    }

    public Double validate(AbstractDistanceWrapper metricWrapper, Double base) {
        List<Double> validationScoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, base);
                DenseMatrix D = metricWrapper.calc(A, parameter);
                if (!hasNaN(D)) {
                    validationScoresByGraph.add(roundScore(graph, D));
                }
                if (getContext().getMetricsStatistics()) {
                    metricStatistics.get(graph).put(base, new MetricStatisticsDTO(D, graph));
                }
            }
        } catch (RuntimeException e) {
            log.error("Calculation error: distance " + metricWrapper.getName() + ", baseParam " + base, e);
        }
        double sum = 0.0;
        for (double validationScore : validationScoresByGraph) {
            sum += validationScore;
        }
        return sum != 0.0 ? sum / validationScoresByGraph.size() : null;
    }

    public Map<Graph, Map<Double, MetricStatisticsDTO>> getMetricStatistics() {
        return metricStatistics;
    }

    protected abstract double roundScore(Graph graph, DenseMatrix D);

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item)) {
                return true;
            }
        }
        return false;
    }

    public abstract GridSearch clone();
}
