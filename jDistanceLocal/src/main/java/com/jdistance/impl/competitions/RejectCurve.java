package com.jdistance.impl.competitions;

import com.jdistance.distance.AbstractMeasureWrapper;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class RejectCurve {
    public Map<String, Map<Double, Double>> calcCurve(AbstractMeasureWrapper wrapper, Double param, GraphBundle graph, Integer pointsCount) {
        Map<String, Map<Double, Double>> agg = new HashMap<>();
        for(int i = 0; i < graph.getGraphs().size(); i++) {
            Map<Double, Double> result = calcCurve(wrapper, param, graph.getGraphs().get(i), pointsCount);
            agg.put(wrapper.getName() + " " + Integer.toString(i), result);
        }
        return agg;
    }
    public Map<Double, Double> calcCurve(AbstractMeasureWrapper wrapper, Double param, Graph graph, Integer pointsCount) {
        Map<Double, Double> curve = new HashMap<>();

        int size = graph.getNodes().size();
        DenseMatrix D = wrapper.calc(graph.getA(), wrapper.getScale().calc(graph.getA(), param));

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Node node1 = graph.getNodes().get(i);
                Node node2 = graph.getNodes().get(j);
                if (node1.getLabel().equals(node2.getLabel())) {
                    sameClusterPairs.add(new ImmutablePair<>(i, j));
                } else {
                    diffClusterPairs.add(new ImmutablePair<>(i, j));
                }
            }
        }

        double min = Arrays.stream(D.getValues()).filter(i -> i != 0).min().orElse(0);
        double max = Arrays.stream(D.getValues()).max().getAsDouble();
        double step = (max - min) / (pointsCount - 1);

        for (double threshold = min; threshold <= max; threshold += step) {
            int sameCount = 0;
            for (Pair<Integer, Integer> sameClusterPair : sameClusterPairs) {
                double dist = D.get(sameClusterPair.getLeft(), sameClusterPair.getRight());
                if (dist <= threshold) {
                    sameCount++;
                }
            }
            int diffCount = 0;
            for (Pair<Integer, Integer> diffClusterPair : diffClusterPairs) {
                double dist = D.get(diffClusterPair.getLeft(), diffClusterPair.getRight());
                if (dist <= threshold) {
                    diffCount++;
                }
            }
            curve.put(((double) sameCount) / sameClusterPairs.size(), ((double) diffCount) / diffClusterPairs.size());
        }

        return curve;
    }
}
