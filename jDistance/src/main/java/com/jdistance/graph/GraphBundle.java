package com.jdistance.graph;

import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.utils.Cloneable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBundle implements Cloneable<GraphBundle> {
    private List<Graph> graphs = new ArrayList<>();
    private GeneratorPropertiesDTO properties;

    public GraphBundle(List<Graph> graphs, GeneratorPropertiesDTO properties) {
        this.graphs = graphs;
        this.properties = properties;
    }

    public String getName() {
        return "Graphs: sizes=" + Arrays.toString(properties.getSizeOfClusters().toArray()) +
                ", probabilities=" + Arrays.deepToString(properties.getProbabilityMatrix()) +
                ", numOfClusters =  " + properties.getSizeOfClusters().size() +
                ", count=" + getCount();
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