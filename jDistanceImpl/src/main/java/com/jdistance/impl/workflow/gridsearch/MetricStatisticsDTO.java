package com.jdistance.impl.workflow.gridsearch;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class MetricStatisticsDTO {
    private Double minValue;
    private Double maxValue;
    private Double avgValue;
    private Map<String, MetricStatisticsDTO> interCluster;
    private Map<Pair<String, String>, MetricStatisticsDTO> intraCluster;

    private MetricStatisticsDTO(Double minValue, Double maxValue, Double avgValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.avgValue = avgValue;
    }

    public MetricStatisticsDTO(DenseMatrix D, Graph graph) {
        fill(D, graph);
    }

    private void fill(DenseMatrix D, Graph graph) {
        MetricStatisticsDTO temp = getMinMaxAvgOfStream(Arrays.stream(D.getValues()));
        minValue = temp.minValue;
        maxValue = temp.maxValue;
        avgValue = temp.avgValue;

        Set<String> clusterLabels = graph.getNodes().stream()
                .map(Node::getLabel)
                .collect(Collectors.toSet());

        interCluster = new TreeMap<>();
        for (String label : clusterLabels) {
            List<Node> clusterNodes = graph.getNodes().stream()
                    .filter(node -> label.equals(node.getLabel()))
                    .collect(Collectors.toList());

            List<Double> interClusterResults = new ArrayList<>();
            for (Node oneNode : clusterNodes) {
                interClusterResults.addAll(clusterNodes.stream()
                        .map(twoNode -> D.get(oneNode.getId(), twoNode.getId()))
                        .collect(Collectors.toList()));
            }
            MetricStatisticsDTO interClusterStatistics = getMinMaxAvgOfStream(interClusterResults.stream().mapToDouble(i -> i));
            interCluster.put(label, interClusterStatistics);
        }

        intraCluster = new TreeMap<>();
        for (String oneLabel : clusterLabels) {
            List<Node> oneClusterNodes = graph.getNodes().stream()
                    .filter(node -> oneLabel.equals(node.getLabel()))
                    .collect(Collectors.toList());
            for (String twoLabel : clusterLabels) {
                List<Node> twoClusterNodes = graph.getNodes().stream()
                        .filter(node -> oneLabel.equals(node.getLabel()))
                        .collect(Collectors.toList());

                List<Double> intraClusterResults = new ArrayList<>();
                for (Node oneNode : oneClusterNodes) {
                    intraClusterResults.addAll(twoClusterNodes.stream()
                            .map(twoNode -> D.get(oneNode.getId(), twoNode.getId()))
                            .collect(Collectors.toList()));
                }
                MetricStatisticsDTO intraClusterStatistics = getMinMaxAvgOfStream(intraClusterResults.stream().mapToDouble(i -> i));
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

    public Map<String, MetricStatisticsDTO> getInterCluster() {
        return interCluster;
    }

    public Map<Pair<String, String>, MetricStatisticsDTO> getIntraCluster() {
        return intraCluster;
    }

    private MetricStatisticsDTO getMinMaxAvgOfStream(DoubleStream stream) {
        OptionalDouble optionalMin = stream.filter(p -> !Double.isNaN(p) && p != 0).min();
        Double minValue = optionalMin.isPresent() ? optionalMin.getAsDouble() : null;
        OptionalDouble optionalMax = stream.filter(p -> !Double.isNaN(p)).max();
        Double maxValue = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
        OptionalDouble optionalAvg = stream.filter(p -> !Double.isNaN(p)).average();
        Double avgValue = optionalAvg.isPresent() ? optionalAvg.getAsDouble() : null;
        return new MetricStatisticsDTO(minValue, maxValue, avgValue);
    }
}
