package com.jdistance.gridsearch.statistics;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;
import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class ClustersMeasureStatistics extends BasicMeasureStatistics {
    private List<Map<Pair<String, String>, BasicMeasureStatistics>> clustersStatisticsByGraph;

    public ClustersMeasureStatistics(BasicMeasureStatistics statistics, List<Map<Pair<String, String>, BasicMeasureStatistics>> clustersStatisticsByGraph) {
        super(statistics);
        this.clustersStatisticsByGraph = clustersStatisticsByGraph;
    }

    public static Map<Pair<String, String>, BasicMeasureStatistics> calcClusterStatisticsForGraph(DenseMatrix D, Graph graph) {
        Map<Pair<String, String>, BasicMeasureStatistics> clustersStatistics = new TreeMap<>();

        List<String> clusterLabels = graph.getNodes().stream()
                .map(Node::getLabel)
                .distinct()
                .collect(Collectors.toList());

        for (int i = 0; i < clusterLabels.size(); i++) {
            String firstLabel = clusterLabels.get(i);
            List<Node> firstClusterNodes = graph.getNodes().stream()
                    .filter(node -> firstLabel.equals(node.getLabel()))
                    .collect(Collectors.toList());
            for (int j = i + 1; j < clusterLabels.size(); j++) {
                String secondLabel = clusterLabels.get(j);
                List<Node> secondClusterNodes = graph.getNodes().stream()
                        .filter(node -> secondLabel.equals(node.getLabel()))
                        .collect(Collectors.toList());

                List<Double> twoClustersRelatingData = new ArrayList<>();
                for (Node firstNode : firstClusterNodes) {
                    twoClustersRelatingData.addAll(secondClusterNodes.stream()
                            .map(secondNode -> D.get(firstNode.getId(), secondNode.getId()))
                            .collect(Collectors.toList()));
                }
                clustersStatistics.put(new ImmutablePair<>(firstLabel, secondLabel), calcMinMaxAvgOfList(twoClustersRelatingData));
            }
        }

        return clustersStatistics;
    }

    public List<Map<Pair<String, String>, BasicMeasureStatistics>> getClustersStatisticsByGraph() {
        return clustersStatisticsByGraph;
    }



}
