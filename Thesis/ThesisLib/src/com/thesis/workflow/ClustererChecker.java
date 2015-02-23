package com.thesis.workflow;


import com.thesis.classifier.Classifier;
import com.thesis.clusterer.Clusterer;
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

public class ClustererChecker {

    Graph graph;
    Integer k;

    public ClustererChecker(Graph graph, Integer k) {
        this.graph = graph;
        this.k = k;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public Map<Double, Double> seriesOfTests(Distance distance, final Double from, final Double to, final Double step) {
        int countOfPoints = (int) Math.round(Math.floor((to - from) / step) + 1);

        Map<Double, Double> results = new HashMap<>();

        Date start = new Date();
        IntStream.range(0, countOfPoints).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double i = from + idx * step;
            Double result = clustererTest(distance, i);
            results.put(i, result);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        System.out.println("time: " + diff + " ms");

        return results;
    }

    public Double clustererTest(Distance distance, Double parameter) {
        ArrayList<SimpleNodeData> simpleNodeData = graph.getSimpleNodeData();

        final DoubleMatrix A = new DoubleMatrix(graph.getSparseM());
        final double[][] D = distance.getD(A, parameter).toArray2();
        final Clusterer clusterer = new Clusterer(D);
        final HashMap<Integer, Integer> data = clusterer.predictClusterer(k);
        Integer countErrors = 0;
        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                if (data.get(j).equals(data.get(k)) == simpleNodeData.get(j).getLabel().equals(simpleNodeData.get(k).getLabel())) {
                    countErrors += 1;
                }
            }
        }

        System.out.println(parameter + ": " + 2 * (double) countErrors / (double) (data.size() * (data.size() - 1)));
        return 1 - (double) countErrors / (double) data.size();
    }
}
