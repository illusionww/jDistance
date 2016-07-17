package com.jdistance.local.adapter.graph;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import org.jblas.DoubleMatrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSVGraphBuilder {
    private String name;
    private List<Node> nodes;
    private DoubleMatrix A;

    public CSVGraphBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CSVGraphBuilder importNodesIdNameClass(String nodesFile) throws IOException {
        nodes = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(nodesFile))) {
            stream.forEach(line -> {
                String[] rawNode = line.split("[\t;]");
                int indexOf = classNames.indexOf(rawNode[2]);
                if (indexOf == -1) {
                    classNames.add(rawNode[2]);
                    indexOf = classNames.size() - 1;
                }
                nodes.add(new Node(Integer.valueOf(rawNode[0]), indexOf));
            });
        }
        return this;
    }

    public CSVGraphBuilder importNodesClassOnly(String nodesFile) throws IOException {
        nodes = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(nodesFile))) {
            List<String> lines = stream.collect(Collectors.toList());
            for (int i = 0; i < lines.size(); i++) {
                String className = lines.get(i);
                int indexOf = classNames.indexOf(className);
                if (indexOf == -1) {
                    classNames.add(className);
                    indexOf = classNames.size() - 1;
                }
                nodes.add(new Node(i, indexOf));
            }
        }
        return this;
    }

    public CSVGraphBuilder importNodesAndEdges(String graphFile) throws IOException {
        nodes = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(graphFile))) {
            List<String> lines = stream.collect(Collectors.toList());
            Integer count = Integer.valueOf(lines.get(0).split(" ")[1]);
            for (int i = 1; i <= count; i++) {
                String[] rawNode = lines.get(i).split(" ");
                int indexOf = classNames.indexOf(rawNode[1]);
                if (indexOf == -1) {
                    classNames.add(rawNode[1]);
                    indexOf = classNames.size() - 1;
                }
                nodes.add(new Node(Integer.valueOf(rawNode[0]), indexOf));
            }
            A = DoubleMatrix.zeros(count, count);
            for (int i = count + 2; i < lines.size() - 1; i++) {
                String[] rawEdge = lines.get(i).split(" ");
                A.put(Integer.decode(rawEdge[0]), Integer.decode(rawEdge[1]), 1);
                A.put(Integer.decode(rawEdge[1]), Integer.decode(rawEdge[0]), 1);
            }
        }
        return this;
    }

    public CSVGraphBuilder importEdgesList(String edgesFile) throws IOException {
        int count = nodes.size();
        A = DoubleMatrix.zeros(count, count);
        try (Stream<String> stream = Files.lines(Paths.get(edgesFile))) {
            stream.forEach(line -> {
                String[] rawEdge = line.split("[\t;]");
                A.put(Integer.decode(rawEdge[0]), Integer.decode(rawEdge[1]), 1);
                A.put(Integer.decode(rawEdge[1]), Integer.decode(rawEdge[0]), 1);
            });
        }
        return this;
    }

    public CSVGraphBuilder importAdjacencyMatrix(String edgesFile) throws IOException {
        int count = nodes.size();
        A = DoubleMatrix.zeros(count, count);
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
            A.put(i, rawSparseMatrix.get(i));
        }
        return this;
    }

    public Graph build() {
        return new Graph(nodes, A);
    }

    public GraphBundle buildBundle() {
        return new Graph(nodes, A).toBundle(name);
    }

    public GraphBundle shuffleAndBuildBundle() {
        Graph graph = new Graph(nodes, A);
        graph.shuffle(10 * nodes.size());
        return graph.toBundle();
    }
}
