package com.jdistance.graph.generator;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;

import java.util.ArrayList;
import java.util.List;

public abstract class GraphGenerator {
    public GraphBundle generate(GeneratorPropertiesDTO properties) {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < properties.getGraphsCount(); i++) {
            Graph graph = generateGraph(properties);
            graphs.add(graph);
        }
        return new GraphBundle(graphs, properties);
    }

    protected abstract Graph generateGraph(GeneratorPropertiesDTO properties);
}
