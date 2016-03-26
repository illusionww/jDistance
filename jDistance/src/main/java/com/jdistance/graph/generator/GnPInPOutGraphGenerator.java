package com.jdistance.graph.generator;

import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GnPInPOutGraphGenerator extends GraphGenerator {
    private static GnPInPOutGraphGenerator instance;
    private final Random random;
    private boolean biDirectional;

    private GnPInPOutGraphGenerator() {
        random = new Random();
        biDirectional = true;
    }

    public static GnPInPOutGraphGenerator getInstance() {
        if (instance == null) {
            instance = new GnPInPOutGraphGenerator();
        }
        return instance;
    }

    protected Graph generateGraph(GeneratorPropertiesDTO properties) {
        int[] sizeClusters = properties.getSizeOfClusters();
        double[][] probabilityMatrix = properties.getProbabilityMatrix();
        int numberOfVertices = 0;
        List<Integer> borderClusters = new LinkedList<>(IntStream.of(sizeClusters).boxed().collect(Collectors.toList()));
        for (int i = 0; i < sizeClusters.length; ++i) {
            numberOfVertices += sizeClusters[i];
            if (i > 0) {
                borderClusters.set(i, borderClusters.get(i - 1) + borderClusters.get(i));
            }
        }

        double[][] sparseMatrix = new double[numberOfVertices][numberOfVertices];
        for (int i = 0; i < numberOfVertices; ++i) {
            for (int j = i + 1; j < numberOfVertices; ++j) {
                sparseMatrix[i][j] = getProbabilityEdge(borderClusters, probabilityMatrix, i, j) > random.nextDouble() ? 1 : 0;
                if (biDirectional) {
                    sparseMatrix[j][i] = sparseMatrix[i][j];
                } else {
                    sparseMatrix[j][i] = getProbabilityEdge(borderClusters, probabilityMatrix, j, i) < random.nextDouble() ? 1 : 0;
                }
            }
        }
        Graph graph = new Graph(generateSimpleNodeDatas(sizeClusters), sparseMatrix);
        graph.shuffle(2*graph.getNodes().size());
        return graph;
    }

    private double getProbabilityEdge(List<Integer> borderClusters, double[][] probabilityMatrix, int from, int to) {
        int fromCluster = 0;
        int toCluster = 0;
        for (int i = 0; i < borderClusters.size(); ++i) {
            if (borderClusters.get(i) < from) {
                fromCluster = i + 1;
            }
            if (borderClusters.get(i) < to) {
                toCluster = i + 1;
            }
            if (borderClusters.get(i) > from && borderClusters.get(i) > to) {
                break;
            }
        }
        return probabilityMatrix[fromCluster][toCluster];
    }

    private ArrayList<Node> generateSimpleNodeDatas(int[] sizeClusters) {
        ArrayList<Node> nodes = new ArrayList<>();
        Integer vertex = 0;
        for (Integer i = 0; i < sizeClusters.length; ++i) {
            for (int j = 0; j < sizeClusters[i]; ++j) {
                nodes.add(new Node(vertex.toString(), i.toString()));
                vertex++;
            }
        }
        return nodes;
    }
}