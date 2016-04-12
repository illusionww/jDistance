package com.jdistance.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class MetricStatistics {
    private Double minValue;
    private Double maxValue;
    private Double avgValue;
    private Map<Pair<String, String>, MetricStatistics> intraCluster;

    public MetricStatistics(DenseMatrix D, Graph graph) {
        fill(D, graph);
    }

    private MetricStatistics(Double minValue, Double maxValue, Double avgValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.avgValue = avgValue;
    }

    private void fill(DenseMatrix D, Graph graph) {
        MetricStatistics temp = getMinMaxAvgOfStream(Arrays.stream(D.getValues()).boxed().collect(Collectors.toList()));
        minValue = temp.minValue;
        maxValue = temp.maxValue;
        avgValue = temp.avgValue;

        Set<String> clusterLabels = graph.getNodes().stream()
                .map(Node::getLabel)
                .collect(Collectors.toSet());

        intraCluster = new TreeMap<>();
        for (String oneLabel : clusterLabels) {
            List<Node> oneClusterNodes = graph.getNodes().stream()
                    .filter(node -> oneLabel.equals(node.getLabel()))
                    .collect(Collectors.toList());
            for (String twoLabel : clusterLabels) {
                List<Node> twoClusterNodes = graph.getNodes().stream()
                        .filter(node -> twoLabel.equals(node.getLabel()))
                        .collect(Collectors.toList());

                List<Double> intraClusterResults = new ArrayList<>();
                for (Node oneNode : oneClusterNodes) {
                    intraClusterResults.addAll(twoClusterNodes.stream()
                            .map(twoNode -> D.get(oneNode.getId(), twoNode.getId()))
                            .collect(Collectors.toList()));
                }
                MetricStatistics intraClusterStatistics = getMinMaxAvgOfStream(intraClusterResults);
                intraCluster.put(new ImmutablePair<>(oneLabel, twoLabel), intraClusterStatistics);
            }
        }
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Double getAvgValue() {
        return avgValue;
    }

    public Map<Pair<String, String>, MetricStatistics> getIntraCluster() {
        return intraCluster;
    }

    private MetricStatistics getMinMaxAvgOfStream(List<Double> list) {
        OptionalDouble optionalMin = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p) && p != 0).min();
        Double minValue = optionalMin.isPresent() ? optionalMin.getAsDouble() : null;
        OptionalDouble optionalMax = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p)).max();
        Double maxValue = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
        OptionalDouble optionalAvg = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p)).average();
        Double avgValue = optionalAvg.isPresent() ? optionalAvg.getAsDouble() : null;
        return new MetricStatistics(minValue, maxValue, avgValue);
    }
}
