package com.thesis.workflow;

import com.thesis.classifier.Classifier;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import com.thesis.metric.Distance;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassifierChecker {
    Graph graph;
    Integer k;
    Double p;

    public ClassifierChecker(Graph graph, Integer k, Double p) {
        this.graph = graph;
        this.k = k;
        this.p = p;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public void setP(Double p) {
        this.p = p;
    }

    public Map<Double, Double> seriesOfTests(Distance distance, final Double from, final Double to, final Double step) {
        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);

        Map<Double, Double> results = new HashMap<>();

        Date start = new Date();
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double i = from + idx * step;
            Double result = classifierTest(distance, i);
            results.put(i, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        System.out.println("time: " + diff + " ms");

        return results;
    }

    public Double classifierTest(Distance distance, Double parameter) {
        ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();

        final DoubleMatrix A = new DoubleMatrix(graph.getSparseM());
        final double[][] D = distance.getD(A, parameter).toArray2();
        final Classifier classifier = new Classifier(D, simpleNodeData);
        final ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p);
        Integer countErrors = 0;
        for (int q = 0; q < data.size(); ++q) {
            SimpleNodeData original = simpleNodeData.get(q);
            SimpleNodeData calculated = data.get(q);
            if (original.getName().equals(calculated.getName()) && !original.getLabel().equals(calculated.getLabel())) {
                countErrors += 1;
            }
        }
        System.out.println(parameter + ": " + (double) countErrors / (double) data.size());
        return (double) countErrors / (double) data.size();
    }
}
