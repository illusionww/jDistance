package com.thesis.workflow;

import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import org.jblas.DoubleMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class Checker {
    List<Graph> graphs;

    public void setGraph(List<Graph> graphs) {
        this.graphs = graphs;
    }

    public Map<Double, Double> seriesOfTests(Distance distance, final Double from, final Double to, final Double step) {
        double border = Math.min(to, distance.getMaxParam());
        int countOfPoints = (int) Math.round(Math.floor((border - from) / step) + 1);

        Map<Double, Double> results = new HashMap<>();

        System.out.println("Start " + distance.getName());
        Date start = new Date();
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double i = from + idx * step;
            Double result = test(distance, i);
            results.put(i, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        System.out.println("End " + distance.getName() + " time: " + diff + " ms");

        return results;
    };

    public Double test(Distance distance, Double parameter) {
        Integer total = 0;
        Integer countErrors = 0;

        for (Graph graph : graphs) {
            ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();

            DoubleMatrix A = new DoubleMatrix(graph.getSparseM());
            double[][] D = distance.getD(A, parameter).toArray2();
            Integer roundErrors = roundErrors(D, simpleNodeData);

            total += simpleNodeData.size();
            countErrors += roundErrors;
        }

        Double rate = 1 - (double) countErrors / (double) total;
        System.out.println(parameter + ": " + rate);

        return rate;
    };

    protected abstract Integer roundErrors(final double[][] D, ArrayList<SimpleNodeData> simpleNodeData);
}
