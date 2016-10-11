package com.jdistance.graph.generator;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphGenerator {
    public GraphBundle generate(GeneratorPropertiesPOJO properties) {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < properties.getGraphsCount(); i++) {
            Graph graph = generateGraph(properties);
            graph.shuffle(10 * properties.getNodesCount());
            graphs.add(graph);
        }
        String name = "graphsCount=" + properties.getGraphsCount() + ", " +
                "nodesCount=" + properties.getNodesCount() + ", " +
                "clustersCount=" + properties.getClustersCount() + ", " +
                "p_in=" + properties.getP_in() + ", " +
                "p_out=" + properties.getP_out();
        return new GraphBundle(name, graphs, properties);
    }

    public GraphBundle generate(String name, GeneratorPropertiesPOJO properties) {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < properties.getGraphsCount(); i++) {
            Graph graph = generateGraph(properties);
            graph.shuffle(10 * properties.getNodesCount());
            graphs.add(graph);
        }
        return new GraphBundle(name, graphs, properties);
    }

    protected abstract Graph generateGraph(GeneratorPropertiesPOJO properties);
}
