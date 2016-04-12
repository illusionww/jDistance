package com.jdistance.graph;

import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import jeigen.DenseMatrix;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Graph {
    private DenseMatrix A;
    private List<Node> nodes;

    public Graph(List<Node> nodes, double[][] A) {
        this(nodes, new DenseMatrix(A));
    }

    public Graph(List<Node> nodes, DenseMatrix A) {
        this.nodes = nodes;
        this.A = A;
    }

    public DenseMatrix getA() {
        return A;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void shuffle(int swapCount) {
        int size = nodes.size();

        Random random = new Random();
        for (int i = 0; i < swapCount; i++) {
            replaceTwoNodes(random.nextInt(size), random.nextInt(size));
        }
    }

    private void replaceTwoNodes(int i, int j) {
        Collections.swap(nodes, i, j);
        for (int k = 0; k < A.rows; k++) {
            double temp = A.get(i, k);
            A.set(i, k, A.get(j, k));
            A.set(j, k, temp);
        }
        for (int k = 0; k < A.rows; k++) {
            double temp = A.get(k, i);
            A.set(k, i, A.get(k, j));
            A.set(k, j, temp);
        }
    }


    public GraphBundle toBundle() {
        int nodesCount = nodes.size();
        int classesCount = nodes.stream().map(Node::getLabel).collect(Collectors.toSet()).size();
        GeneratorPropertiesPOJO properties = new GeneratorPropertiesPOJO(1, nodesCount, classesCount, 0, 0);
        return new GraphBundle(Collections.singletonList(this), properties);
    }

    public GraphBundle toBundle(String name) {
        int nodesCount = nodes.size();
        int classesCount = nodes.stream().map(Node::getLabel).collect(Collectors.toSet()).size();
        GeneratorPropertiesPOJO properties = new GeneratorPropertiesPOJO(1, nodesCount, classesCount, 0, 0);
        return new GraphBundle(name, Collections.singletonList(this), properties);
    }
}
