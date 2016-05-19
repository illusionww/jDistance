package com.jdistance.graph;

import com.jdistance.graph.generator.GeneratorPropertiesPOJO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphBundle implements Cloneable, Serializable {
    private String name;
    private List<Graph> graphs;
    private GeneratorPropertiesPOJO properties;

    public GraphBundle(List<Graph> graphs, GeneratorPropertiesPOJO properties) {
        this.graphs = graphs;
        this.properties = properties;
        this.name = "Graphs: " +
            "graphsCount=" + properties.getGraphsCount() + ", " +
            "nodesCount=" + properties.getNodesCount() + ", " +
            "clustersCount=" + properties.getClustersCount() + ", " +
            "p_in=" + properties.getP_in() + ", " +
            "p_out=" + properties.getP_out();
    }

    public GraphBundle(String name, List<Graph> graphs, GeneratorPropertiesPOJO properties) {
        this.name = name;
        this.graphs = graphs;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public GeneratorPropertiesPOJO getProperties() {
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