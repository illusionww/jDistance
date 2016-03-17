package com.jdistance.impl.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import jeigen.DenseMatrix;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVGraphBuilder {
    private List<Node> nodes;
    private DenseMatrix sparseMatrix;

    public CSVGraphBuilder importNodesIdNameClass(String nodesFile) throws IOException {
        nodes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(nodesFile))) {
            stream.forEach(line -> {
                String[] rawNode = line.split("[\t;]");
                nodes.add(new Node(rawNode[0], rawNode[2]));
            });
        }
        return this;
    }

    public CSVGraphBuilder importNodesClassOnly(String nodesFile) throws IOException {
        nodes = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(nodesFile))) {
            List<String> lines = stream.collect(Collectors.toList());
            for (int i = 0; i < lines.size(); i++) {
                String clazz = lines.get(i);
                nodes.add(new Node(Integer.toString(i), clazz));
            }
            ;
        }
        return this;
    }

    public CSVGraphBuilder importEdgesList(String edgesFile) throws ParserConfigurationException, IOException, SAXException {
        int count = nodes.size();
        sparseMatrix = DenseMatrix.zeros(count, count);
        try (Stream<String> stream = Files.lines(Paths.get(edgesFile))) {
            stream.forEach(line -> {
                String[] rawEdge = line.split("[\t;]");
                sparseMatrix.set(Integer.decode(rawEdge[0]), Integer.decode(rawEdge[1]), 1);
                sparseMatrix.set(Integer.decode(rawEdge[1]), Integer.decode(rawEdge[0]), 1);
            });
        }
        return this;
    }

    public CSVGraphBuilder importAdjacencyMatrix(String edgesFile) throws ParserConfigurationException, IOException, SAXException {
        int count = nodes.size();
        sparseMatrix = DenseMatrix.zeros(count, count);
        List<Double> rawSparseMatrix = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(edgesFile))) {
            stream.forEach(line -> {
                List<Double> rawLine = Arrays.stream(line.split("[,]"))
                        .map(Double::valueOf)
                        .collect(Collectors.toList());
                rawSparseMatrix.addAll(rawLine);
            });
        }
        for (int i = 0; i < rawSparseMatrix.size(); i++) {
            sparseMatrix.set(i, rawSparseMatrix.get(i));
        }
        return this;
    }

    public Graph build() {
        return new Graph(sparseMatrix, nodes);
    }

    public GraphBundle buildBundle() {
        Graph graph = new Graph(sparseMatrix, nodes);
        int nodesCount = nodes.size();
        int classesCount = nodes.stream().map(Node::getLabel).collect(Collectors.toSet()).size();
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(1, nodesCount, classesCount, 0, 0);
        return new GraphBundle(Collections.singletonList(graph), properties);
    }

}
