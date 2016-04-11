package com.jdistance.graph;

import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.utils.Cloneable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBundle implements Cloneable<GraphBundle> {
    private String name;
    private List<Graph> graphs;
    private GeneratorPropertiesDTO properties;

    public GraphBundle(List<Graph> graphs, GeneratorPropertiesDTO properties) {
        this.graphs = graphs;
        this.properties = properties;
        this.name = "Graphs: " +
            "graphsCount=" + properties.getGraphsCount() + ", " +
            "nodesCount=" + properties.getNodesCount() + ", " +
            "clustersCount=" + properties.getClustersCount();
    }

    public GraphBundle(String name, List<Graph> graphs, GeneratorPropertiesDTO properties) {
        this.name = name;
        this.graphs = graphs;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public GeneratorPropertiesDTO getProperties() {
        return properties;
    }

    public Integer getCount() {
        return this.graphs.size();
    }

    public List<Graph> getGraphs() {
        return graphs;
    }

    public void setGraphs(List<Graph> graphs) {
        this.graphs = graphs;
    }



    @Override
    public GraphBundle clone() {
        List<Graph> clone = new ArrayList<>(graphs);
        return new GraphBundle(clone, properties);
    }
}