package com.jdistance.local;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.local.workflow.Context;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RejectCurve {
    private static final Logger log = LoggerFactory.getLogger(RejectCurve.class);

    public Map<String, Map<Double, Double>> calcCurve(AbstractMeasureWrapper wrapper, Double param, GraphBundle graph, Integer pointsCount) {
        Map<String, Map<Double, Double>> agg = new HashMap<>();
        for (int i = 0; i < graph.getGraphs().size(); i++) {
            Map<Double, Double> result = calcCurve(wrapper, param, graph.getGraphs().get(i), pointsCount);
            agg.put(wrapper.getName() + " " + Integer.toString(i), result);
        }
        return agg;
    }

    public Map<Double, Double> calcCurve(AbstractMeasureWrapper wrapper, Double param, Graph graph, Integer pointsCount) {
        DenseMatrix D = wrapper.calc(graph.getA(), wrapper.getScale().calc(graph.getA(), param));
//        D = D.mul(-1);

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < graph.getVertices().size(); i++) {
            for (int j = i + 1; j < graph.getVertices().size(); j++) {
                Integer node1 = graph.getVertices().get(i);
                Integer node2 = graph.getVertices().get(j);
                if (Objects.equals(node1, node2)) {
                    sameClusterPairs.add(new ImmutablePair<>(i, j));
                } else {
                    diffClusterPairs.add(new ImmutablePair<>(i, j));
                }
            }
        }
        int sameClusterPairsSize = sameClusterPairs.size();
        int diffClusterPairsSize = diffClusterPairs.size();

        Map<Double, Double> curve = new HashMap<>();
        for (double threshold : Arrays.stream(D.getValues()).filter(i -> !new Double(i).isInfinite()).sorted().toArray()) {
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
            curve.put(((double) diffCount) / diffClusterPairsSize, ((double) sameCount) / sameClusterPairsSize);
        }
        return curve;
    }

    public void writeData(Map<String, Map<Double, Double>> data, List<String> taskNames, String filename) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(filename, "csv")))) {
            Set<Double> points = new TreeSet<>();
            data.values().forEach(scores -> points.addAll(scores.keySet()));

            outputWriter.write("param\t");
            for (String taskName : taskNames) {
                outputWriter.write(taskName + "\t");
            }
            outputWriter.newLine();
            for (Double key : points) {
                outputWriter.write(key + "\t");
                for (String taskName : taskNames) {
                    outputWriter.write(data.get(taskName).get(key) + "\t");
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            log.error("IOException while write results", e);
        }
    }
}
