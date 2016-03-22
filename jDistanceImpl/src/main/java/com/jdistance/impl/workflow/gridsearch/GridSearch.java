package com.jdistance.impl.workflow.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import com.jdistance.graph.GraphBundle;
import com.jdistance.metric.MetricWrapper;
import com.jdistance.utils.Cloneable;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class GridSearch implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(GridSearch.class);

    public abstract String getName();

    public abstract GraphBundle getGraphBundle();

    public Map<Double, Double> seriesOfTests(final MetricWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        Date start = new Date();
        log.debug("START {}", metricWrapper.getName());

        double step = (to - from) / (pointsCount - 1);
        final Map<Double, Double> validationScores = new HashMap<>();
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
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

    public Double validate(MetricWrapper metricWrapper, Double base) {
        List<Double> validationScoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : getGraphBundle().getGraphs()) {
                List<Node> nodesData = graph.getNodes();
                DenseMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, base);
                DenseMatrix D = metricWrapper.getMetric().getD(A, parameter);
                if (!hasNaN(D)) {
                    validationScoresByGraph.add(roundScore(graph, D, nodesData));
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

    protected abstract double roundScore(Graph graph, DenseMatrix D, List<Node> node);

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item) || Double.isInfinite(item)) {
                return true;
            }
        }
        return false;
    }

    public abstract GridSearch clone();
}
