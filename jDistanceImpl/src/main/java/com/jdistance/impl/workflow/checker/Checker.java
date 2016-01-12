package com.jdistance.impl.workflow.checker;

import com.jdistance.graph.Graph;
import com.jdistance.graph.NodeData;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.metric.MetricWrapper;
import com.jdistance.utils.Cloneable;
import com.jdistance.utils.MatrixUtils;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Checker implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(Checker.class);

    public abstract String getName();

    public abstract GraphBundle getGraphBundle();

    public Map<Double, Double> seriesOfTests(final MetricWrapper metricWrapper, Double from, Double to, Integer pointsCount) {
        final Map<Double, Double> results = new ConcurrentHashMap<>();

        Date start = new Date();
        log.debug("START {}", metricWrapper.getName());

        double step = (to - from) / (pointsCount - 1);
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double result = test(metricWrapper, base);
            results.put(base, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.debug("END {}; time: {} ms", metricWrapper.getName(), diff);
        return results;
    }

    public Double test(MetricWrapper metricWrapper, Double base) {
        List<CheckerTestResultDTO> results = new ArrayList<>();
        try {
            for (Graph graph : getGraphBundle().getGraphs()) {
                ArrayList<NodeData> nodesData = graph.getNodeData();

                DenseMatrix A = graph.getSparseMatrix();
                Double parameter = metricWrapper.getScale().calc(A, base);
                DenseMatrix D = metricWrapper.getMetric().getD(A, parameter);
                if (!hasNaN(D)) {
                    CheckerTestResultDTO result = roundErrors(graph, D, nodesData);
                    results.add(result);
                }
            }
        } catch (RuntimeException e) {
            log.error("Calculation error: distance " + metricWrapper.getName() + ", baseParam " + base, e);
        }
        Double rate = rate(results);
        log.info("{}: {} {}", metricWrapper.getName(), base, rate);

        return rate;
    }

    protected abstract CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, ArrayList<NodeData> nodeData);

    protected Double rate(List<CheckerTestResultDTO> results) {
        Double sum = 0.0;
        for (CheckerTestResultDTO result : results) {
            Double total = result.getTotal();
            Double countErrors = result.getCountErrors();
            Double coloredNodes = result.getColoredNodes();
            sum += 1 - countErrors / (total - coloredNodes);
        }
        return sum / (double) results.size();
    }

    protected boolean hasNaN(DenseMatrix D) {
        boolean nan = false;
        for (double[] row : MatrixUtils.toArray2(D)) {
            for (double item : row) {
                if (Double.isNaN(item) || Double.isInfinite(item)) {
                    nan = true;
                    break;
                }
            }
            if (nan) break;
        }
        return nan;
    }

    public abstract Checker clone();
}
