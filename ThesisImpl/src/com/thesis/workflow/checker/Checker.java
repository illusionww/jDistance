package com.thesis.workflow.checker;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import com.thesis.metric.DistancesHelper;
import com.thesis.utils.Cloneable;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Checker implements Cloneable {
    private static final Logger log = LoggerFactory.getLogger(Checker.class);

    public abstract String getName();

    public abstract CheckerType getType();

    public abstract GraphBundle getGraphBundle();

    public Map<Double, Double> seriesOfTests(final Distance distance, Double from, Double to, Integer pointsCount) {
        final Map<Double, Double> results = new ConcurrentHashMap<>();

        Date start = new Date();
        log.debug("START {}", distance.getName());

        double step = (to - from) / (pointsCount - 1);
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double i = distance.getScale().calc(base);
            Double result = test(distance, i);
            results.put(base, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.debug("END {}; time: {} ms", distance.getName(), diff);
        return results;
    }

    public Double test(Distance distance, Double parameter) {
        List<CheckerTestResultDTO> results = new ArrayList<>();
        try {
            for (Graph graph : getGraphBundle().getGraphs()) {
                ArrayList<SimpleNodeData> nodesData = graph.getSimpleNodeData();

                DenseMatrix A = graph.getSparseMatrix();
                DenseMatrix D = distance.getD(A, parameter);
                if (!hasNaN(D)) {
                    CheckerTestResultDTO result = roundErrors(D, nodesData);
                    results.add(result);
                }
            }
        } catch (RuntimeException e) {
            log.error("Calculation error: disnance " + distance.getName() + ", param " + parameter, e);
        }

        Double rate = rate(results);
//        log.info("{}: {} {}", distance.getName(), parameter, rate);

        return rate;
    }

    protected abstract CheckerTestResultDTO roundErrors(DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData);

    protected Double rate(List<CheckerTestResultDTO> results) {
        Double sum = 0.0;
        for (CheckerTestResultDTO result : results) {
            int total = result.getTotal();
            int countErrors = result.getCountErrors();
            int coloredNodes = result.getColoredNodes();

            sum += 1 - (double)countErrors/(double)(total - coloredNodes);
        }
        return sum / (double) results.size();
    }

    protected boolean hasNaN(DenseMatrix D) {
        boolean nan = false;
        for (double[] row : DistancesHelper.toArray2(D)) {
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
