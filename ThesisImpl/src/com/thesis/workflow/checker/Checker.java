package com.thesis.workflow.checker;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import com.thesis.metric.DistancesHelper;
import com.thesis.metric.Scale;
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

    public Map<Double, Double> seriesOfTests(final Distance distance, Double from, Double to, Double step, Scale scale) {
        final Map<Double, Double> results = new ConcurrentHashMap<>();

        Date start = new Date();
        log.debug("START {}", distance.getName());

        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double i = scale.calc(base);
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
        parameter = parameter < 0.0001 ? 0.0001 : parameter;
        try {
            for (Graph graph : getGraphBundle().getGraphs()) {
                ArrayList<SimpleNodeData> nodesData = graph.getSimpleNodeData();

                DenseMatrix A = graph.getSparseMatrix();
                DenseMatrix D = distance.getD(A, parameter);
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
                if (!nan) {
                    CheckerTestResultDTO result = roundErrors(D, nodesData);
                    results.add(result);
                }
            }
        } catch (RuntimeException e) {
            log.error("Calculation error: disnance " + distance.getShortName() + ", param " + parameter, e);
        }

        Double rate = rate(results);
        log.debug("{}: {} {}", distance.getShortName(), parameter, rate);

        return rate;
    }

    protected abstract CheckerTestResultDTO roundErrors(DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData);

    protected abstract Double rate(List<CheckerTestResultDTO> results);

    public abstract Checker clone();
}
