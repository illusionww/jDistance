package com.jdistance.impl.adapter.generator;

import com.jdistance.graph.Graph;
import com.jdistance.utils.Cloneable;
import com.graphgenerator.utils.Input;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBundle implements Cloneable<GraphBundle> {
    Input input;
    List<Graph> graphs = new ArrayList<>();

    public GraphBundle(Input input, List<Graph> graphs) {
        this.input = input;
        this.graphs = graphs;
    }

    public GraphBundle(Input input, Integer count) {
        this.input = input;
        generate(count);
    }

    public String getName() {
        return "n = " + Arrays.toString(input.getSizeOfVertices().toArray()) + "probabilities = " +
                Arrays.toString(input.getProbabilityMatrix())+ " numOfClusters =  " +
                input.getSizeOfVertices().size() + " count = " + getCount();
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

    public void generate(int count) {
        GraphGeneratorAdapter generator = new GraphGeneratorAdapter();
        graphs = generator.generateList(count, input);
    }

    @Override
    public GraphBundle clone() {
        List graphsCopy = new ArrayList<>(graphs);
        return new GraphBundle(input, graphsCopy);
    }
}