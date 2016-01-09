package com.jdistance.impl.adapter.generator;

import com.graphgenerator.generator.GraphGenerator;
import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.jdistance.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class GraphGeneratorAdapter {
    private static final Logger log = LoggerFactory.getLogger(GraphGeneratorAdapter.class);


    public List<Graph> generateList(int count, GeneratorPropertiesDTO generatorPropertiesDTO) {
        List<Graph> graphs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            graphs.add(generate(generatorPropertiesDTO));
            log.debug(i + " graph successfully generate");
        }
        return graphs;
    }

    public Graph generate(GeneratorPropertiesDTO generatorPropertiesDTO) {
        return GraphGenerator.getInstance().generateGraph(generatorPropertiesDTO);
    }
}