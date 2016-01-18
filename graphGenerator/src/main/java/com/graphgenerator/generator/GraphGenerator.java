package com.graphgenerator.generator;

import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.jdistance.graph.Graph;
import com.jdistance.graph.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GraphGenerator {
    private static GraphGenerator graphGenerator;
    private final Random random;
    private boolean biDirectional;

    private GraphGenerator() {
        random = new Random();
        biDirectional = true;
    }

    public static GraphGenerator getInstance() {
        if (graphGenerator == null) {
            graphGenerator = new GraphGenerator();
        }
        return graphGenerator;
    }

    public void setBiDirectional(boolean b) {
        biDirectional = b;
    }

    public Graph generateGraph(List<Integer> sizeClusters, double[][] probabilityMatrix) {
        int numberOfVertices = 0;
        List<Integer> borderClusters = new LinkedList<>(sizeClusters);
        for (int i = 0; i < sizeClusters.size(); ++i) {
            numberOfVertices += sizeClusters.get(i);
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
        return new Graph(sparseMatrix, generateSimpleNodeDatas(sizeClusters));
    }

    public Graph generateGraph(GeneratorPropertiesDTO generatorPropertiesDTO) {
        return generateGraph(generatorPropertiesDTO.getSizeOfClusters(), generatorPropertiesDTO.getProbabilityMatrix());
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

    private ArrayList<Node> generateSimpleNodeDatas(List<Integer> sizeClusters) {
        ArrayList<Node> nodes = new ArrayList<>();
        Integer vertex = 0;
        for (Integer i = 0; i < sizeClusters.size(); ++i) {
            for (int j = 0; j < sizeClusters.get(i); ++j) {
                nodes.add(new Node(vertex.toString(), i.toString()));
                vertex++;
            }
        }
        return nodes;
    }
}