package com.thesis.workflow.checker;

import com.thesis.adapter.parser.graph.Graph;
import com.thesis.adapter.parser.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import org.jblas.DoubleMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Checker {
    public abstract List<Graph> getGraphs();

    public Map<Double, Double> seriesOfTests(Distance distance, Double from, Double to, Double step, Scale scale) {
        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);

        Map<Double, Double> results = new HashMap<>();

        System.out.println("Start \"" + distance.getName() + "\"");
        Date start = new Date();
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double base = from + idx * step;
            Double i = scale.calc(base);
            Double result = test(distance, i);
            results.put(base, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        System.out.println("End \"" + distance.getName() + "\"; time: " + diff + " ms");

        return results;
    };

    public Double test(Distance distance, Double parameter) {
        Integer total = 0;
        Integer countErrors = 0;

        for (Graph graph : getGraphs()) {
            ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();

            DoubleMatrix A = new DoubleMatrix(graph.getSparseM());
            double[][] D = distance.getD(A, parameter).toArray2();
            Integer[] result = roundErrors(D, simpleNodeData);

            total += result[0];
            countErrors += result[1];
        }

        Double rate = rate((double)countErrors, (double)total);
        System.out.println(distance.getShortName() + ": " + parameter + " " + rate);

        return rate;
    };

    protected abstract Integer[] roundErrors(final double[][] D, ArrayList<SimpleNodeData> simpleNodeData);

    protected abstract Double rate(Double countErrors, Double total);
}
