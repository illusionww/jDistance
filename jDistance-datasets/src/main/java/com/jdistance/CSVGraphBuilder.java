package com.jdistance;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import jeigen.DenseMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVGraphBuilder {
    private String name;
    private List<Integer> vertices;
    private DenseMatrix A;
    private ClassLoader classLoader;

    public CSVGraphBuilder() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public CSVGraphBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public CSVGraphBuilder importNodesIdNameClass(String nodesFile) throws IOException {
        vertices = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        InputStream fileStream = classLoader.getResourceAsStream(nodesFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream))) {
            buffer.lines().forEach(line -> {
                String[] rawNode = line.split("[\t;]");
                int indexOf = classNames.indexOf(rawNode[2]);
                if (indexOf == -1) {
                    classNames.add(rawNode[2]);
                    indexOf = classNames.size() - 1;
                }
                vertices.add(indexOf);
            });
        }
        return this;
    }

    public CSVGraphBuilder importNodesClassOnly(String nodesFile) throws IOException {
        vertices = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        InputStream fileStream = classLoader.getResourceAsStream(nodesFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream))) {
            List<String> lines = buffer.lines().collect(Collectors.toList());
            for (int i = 0; i < lines.size(); i++) {
                String className = lines.get(i);
                int indexOf = classNames.indexOf(className);
                if (indexOf == -1) {
                    classNames.add(className);
                    indexOf = classNames.size() - 1;
                }
                vertices.add(indexOf);
            }
        }
        return this;
    }

    public CSVGraphBuilder importNodesAndEdges(String graphFile) throws IOException {
        vertices = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        InputStream fileStream = classLoader.getResourceAsStream(graphFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream))) {
            List<String> lines = buffer.lines().collect(Collectors.toList());
            Integer count = Integer.valueOf(lines.get(0).split(" ")[1]);
            for (int i = 1; i <= count; i++) {
                String[] rawNode = lines.get(i).split(" ");
                int indexOf = classNames.indexOf(rawNode[1]);
                if (indexOf == -1) {
                    classNames.add(rawNode[1]);
                    indexOf = classNames.size() - 1;
                }
                vertices.add(indexOf);
            }
            A = DenseMatrix.zeros(count, count);
            for (int i = count + 2; i < lines.size() - 1; i++) {
                String[] rawEdge = lines.get(i).split(" ");
                A.set(Integer.decode(rawEdge[0]) - 1, Integer.decode(rawEdge[1]) - 1, 1);
                A.set(Integer.decode(rawEdge[1]) - 1, Integer.decode(rawEdge[0]) - 1, 1);
            }
        }
        return this;
    }

    public CSVGraphBuilder importEdgesList(String edgesFile) throws IOException {
        int count = vertices.size();
        A = DenseMatrix.zeros(count, count);
        InputStream fileStream = classLoader.getResourceAsStream(edgesFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream))) {
            buffer.lines().forEach(line -> {
                String[] rawEdge = line.split("[\t;]");
                A.set(Integer.decode(rawEdge[0]), Integer.decode(rawEdge[1]), 1);
                A.set(Integer.decode(rawEdge[1]), Integer.decode(rawEdge[0]), 1);
            });
        }
        return this;
    }

    public CSVGraphBuilder importAdjacencyMatrix(String edgesFile) throws IOException {
        int count = vertices.size();
        A = DenseMatrix.zeros(count, count);
        List<Double> rawSparseMatrix = new ArrayList<>();
        InputStream fileStream = classLoader.getResourceAsStream(edgesFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(fileStream))) {
            buffer.lines().forEach(line -> {
                List<Double> rawLine = Arrays.stream(line.split("[,]"))
                        .map(Double::valueOf)
                        .collect(Collectors.toList());
                rawSparseMatrix.addAll(rawLine);
            });
        }
        for (int i = 0; i < rawSparseMatrix.size(); i++) {
            A.set(i, rawSparseMatrix.get(i));
        }
        return this;
    }

    public Graph build() {
        return new Graph(vertices, A);
    }

    public GraphBundle buildBundle() {
        return new Graph(vertices, A).toBundle(name);
    }

    public GraphBundle shuffleAndBuildBundle() {
        Graph graph = new Graph(vertices, A);
        graph.shuffle(10 * vertices.size());
        return graph.toBundle(name);
    }
}
