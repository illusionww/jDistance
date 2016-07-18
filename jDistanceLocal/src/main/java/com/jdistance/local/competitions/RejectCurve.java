package com.jdistance.local.competitions;

import com.jdistance.measure.AbstractMeasureWrapper;
import com.jdistance.measure.Distance;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.local.workflow.Context;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RejectCurve {
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
        D = D.mul(-1);

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j = i + 1; j < graph.getNodes().size(); j++) {
                Node node1 = graph.getNodes().get(i);
                Node node2 = graph.getNodes().get(j);
                if (node1.getLabel() == node2.getLabel()) {
                    sameClusterPairs.add(new ImmutablePair<>(i, j));
                } else {
                    diffClusterPairs.add(new ImmutablePair<>(i, j));
                }
            }
        }
        int sameClusterPairsSize = sameClusterPairs.size();
        int diffClusterPairsSize = diffClusterPairs.size();

        double min = Arrays.stream(D.getValues()).filter(i -> i != 0).min().orElse(0);
        double max = Arrays.stream(D.getValues()).filter(i -> !new Double(i).isInfinite()).max().getAsDouble();
        double step = (max - min) / (pointsCount - 1);

        Map<Double, Double> curve = new HashMap<>();
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
            curve.put(((double) diffCount) / diffClusterPairsSize, ((double) sameCount) / sameClusterPairsSize);
        }
        return curve;
    }

    public void writeDistributionBySP(Graph graph) {
        DenseMatrix D = Distance.SP_CT.getD(graph.getA(), 0);
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("distances", "csv")))) {
            outputWriter.write("distance\n");
            for (int i = 0; i < D.cols; i++) {
                for (int j = i + 1; j < D.rows; j++) {
                    outputWriter.write(Double.toString(D.get(i, j)) + "\n");
                }
            }
        } catch (IOException ignored) {
        }

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j = i + 1; j < graph.getNodes().size(); j++) {
                Node node1 = graph.getNodes().get(i);
                Node node2 = graph.getNodes().get(j);
                if (node1.getLabel() == node2.getLabel()) {
                    sameClusterPairs.add(new ImmutablePair<>(i, j));
                } else {
                    diffClusterPairs.add(new ImmutablePair<>(i, j));
                }
            }
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("same_distances", "csv")))) {
            outputWriter.write("same\n");
            for (Pair<Integer, Integer> sameClusterPair : sameClusterPairs) {
                outputWriter.write(Double.toString(D.get(sameClusterPair.getLeft(), sameClusterPair.getRight())) + "\n");
            }
        } catch (IOException ignored) {
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("diff_distances", "csv")))) {
            outputWriter.write("diff\n");
            for (Pair<Integer, Integer> diffClusterPair : diffClusterPairs) {
                outputWriter.write(Double.toString(D.get(diffClusterPair.getLeft(), diffClusterPair.getRight())) + "\n");
            }
        } catch (IOException ignored) {
        }
    }

    public void writeVerticesDegrees(Graph graph) {
        Map<Integer, Double> degrees = new TreeMap<>();
        for (int i = 0; i < graph.getA().cols; i++) {
            Double degree = 0.0;
            for (int j = 0; j < graph.getA().rows; j++) {
                if (i != j && graph.getA().get(i, j) > 0) {
                    degree++;
                }
            }
            degrees.put(i, degree);
        }
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("degrees", "csv")))) {
            outputWriter.write("degree\n");
            for (Double degree : degrees.values()) {
                outputWriter.write(degree + "\n");
            }
            outputWriter.newLine();
        } catch (IOException ignored) {
        }

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < graph.getNodes().size(); i++) {
            for (int j = i + 1; j < graph.getNodes().size(); j++) {
                if (graph.getA().get(i, j) > 0) {
                    Node node1 = graph.getNodes().get(i);
                    Node node2 = graph.getNodes().get(j);
                    if (node1.getLabel() == node2.getLabel()) {
                        sameClusterPairs.add(new ImmutablePair<>(i, j));
                    } else {
                        diffClusterPairs.add(new ImmutablePair<>(i, j));
                    }
                }
            }
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("same_degrees", "csv")))) {
            outputWriter.write("same\n");
            for (Pair<Integer, Integer> sameClusterPair : sameClusterPairs) {
                outputWriter.write(degrees.get(sameClusterPair.getLeft()) + "\n");
                outputWriter.write(degrees.get(sameClusterPair.getRight()) + "\n");
            }
        } catch (IOException ignored) {
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("diff_degrees", "csv")))) {
            outputWriter.write("diff\n");
            for (Pair<Integer, Integer> diffClusterPair : diffClusterPairs) {
                outputWriter.write(degrees.get(diffClusterPair.getLeft()) + "\n");
                outputWriter.write(degrees.get(diffClusterPair.getRight()) + "\n");
            }
        } catch (IOException ignored) {
        }
    }

    public void writeVerticesDegreesWithoutRepeat(DenseMatrix A, List<Node> nodes) {
        Map<Integer, Double> degrees = new TreeMap<>();
        for (int i = 0; i < A.cols; i++) {
            Double degree = 0.0;
            for (int j = 0; j < A.rows; j++) {
                if (i != j && A.get(i, j) > 0) {
                    degree++;
                }
            }
            degrees.put(i, degree);
        }
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("degrees_wr", "csv")))) {
            outputWriter.write("degree\n");
            for (Double degree : degrees.values()) {
                outputWriter.write(degree + "\n");
            }
            outputWriter.newLine();
        } catch (IOException ignored) {
        }

        List<Pair<Integer, Integer>> sameClusterPairs = new ArrayList<>();
        List<Pair<Integer, Integer>> diffClusterPairs = new ArrayList<>();
        for (int i = 0; i < A.cols; i++) {
            for (int j = i + 1; j < A.cols; j++) {
                if (A.get(i, j) > 0) {
                    Node node1 = nodes.get(i);
                    Node node2 = nodes.get(j);
                    if (node1.getLabel() == node2.getLabel()) {
                        sameClusterPairs.add(new ImmutablePair<>(i, j));
                    } else {
                        diffClusterPairs.add(new ImmutablePair<>(i, j));
                    }
                }
            }
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("same_degrees_wr", "csv")))) {
            outputWriter.write("same\n");
            Map<Integer, Double> sameDegrees = new TreeMap<>();
            for (Pair<Integer, Integer> sameClusterPair : sameClusterPairs) {
                sameDegrees.put(sameClusterPair.getLeft(), degrees.get(sameClusterPair.getLeft()));
                sameDegrees.put(sameClusterPair.getRight(), degrees.get(sameClusterPair.getRight()));
            }
            for (Double sameDegree : sameDegrees.values()) {
                outputWriter.write(sameDegree + "\n");
            }
        } catch (IOException ignored) {
        }

        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName("diff_degrees_wr", "csv")))) {
            outputWriter.write("diff\n");
            Map<Integer, Double> diffDegrees = new TreeMap<>();
            for (Pair<Integer, Integer> diffClusterPair : diffClusterPairs) {
                diffDegrees.put(diffClusterPair.getLeft(), degrees.get(diffClusterPair.getLeft()));
                diffDegrees.put(diffClusterPair.getRight(), degrees.get(diffClusterPair.getRight()));
            }
            for (Double diffDegree : diffDegrees.values()) {
                outputWriter.write(diffDegree + "\n");
            }
        } catch (IOException ignored) {
        }
    }
}
