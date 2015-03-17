package com.thesis.adapter.parser;

import com.thesis.adapter.parser.graph.Graph;
import com.thesis.adapter.parser.graph.SimpleNodeData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphValidator {
    public static boolean validate(Graph graph, int numOfNodes, int numOfClusters, int deviation) {
        List<SimpleNodeData> data = graph.getSimpleNodeData();

        // validate number of nodes
        if (data.size() + deviation < numOfNodes || data.size() - deviation > numOfNodes) {
            return false;
        }

        // validate number of clusters
        Set<String> labels = new HashSet<>();
        data.forEach(item -> labels.add(item.getLabel()));
        return labels.size() == numOfClusters;
    }
}
