package com.jdistance.impl.adapter.generator;

import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.jdistance.graph.Graph;
import com.jdistance.utils.Cloneable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphBundle implements Cloneable<GraphBundle> {
    GeneratorPropertiesDTO generatorPropertiesDTO;
    List<Graph> graphs = new ArrayList<>();

    public GraphBundle(GeneratorPropertiesDTO generatorPropertiesDTO, List<Graph> graphs) {
        this.generatorPropertiesDTO = generatorPropertiesDTO;
        this.graphs = graphs;
    }

    public GraphBundle(GeneratorPropertiesDTO generatorPropertiesDTO, Integer count) {
        this.generatorPropertiesDTO = generatorPropertiesDTO;
        generate(count);
    }

    public String getName() {
        return "Graphs: sizes=" + Arrays.toString(generatorPropertiesDTO.getSizeOfClusters().toArray()) +
                ", probabilities=" + Arrays.deepToString(generatorPropertiesDTO.getProbabilityMatrix()) +
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

    public void generate(int count) {
        GraphGeneratorAdapter generator = new GraphGeneratorAdapter();
        graphs = generator.generateList(count, generatorPropertiesDTO);
    }

    @Override
    public GraphBundle clone() {
        List graphsCopy = new ArrayList<>(graphs);
        return new GraphBundle(generatorPropertiesDTO, graphsCopy);
    }
}