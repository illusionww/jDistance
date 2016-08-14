package com.jdistance.graph;

import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import jeigen.DenseMatrix;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Graph implements Serializable {
    private DenseMatrix A;
    private List<Integer> vertices;

    public Graph(List<Integer> vertices, double[][] A) {
        this.vertices = vertices;
        this.A = new DenseMatrix(A);
    }

    public Graph(List<Integer> vertices, DenseMatrix A) {
        this.vertices = vertices;
        this.A = A;
    }

    public DenseMatrix getA() {
        return A;
    }

    public List<Integer> getVertices() {
        return vertices;
    }

    public Graph shuffle(int swapCount) {
        int size = vertices.size();

        Random random = new Random();
        for (int i = 0; i < swapCount; i++) {
            replaceTwoNodes(random.nextInt(size), random.nextInt(size));
        }
        return this;
    }

    private void replaceTwoNodes(int i, int j) {
        Collections.swap(vertices, i, j);
        for (int k = 0; k < A.cols; k++) {
            double temp = A.get(i, k);
            A.set(i, k, A.get(j, k));
            A.set(j, k, temp);
        }
        for (int k = 0; k < A.cols; k++) {
            double temp = A.get(k, i);
            A.set(k, i, A.get(k, j));
            A.set(k, j, temp);
        }
    }


    public GraphBundle toBundle() {
        int nodesCount = vertices.size();
        int classesCount = vertices.stream().collect(Collectors.toSet()).size();
        GeneratorPropertiesPOJO properties = new GeneratorPropertiesPOJO(1, nodesCount, classesCount, 0, 0);
        return new GraphBundle(Collections.singletonList(this), properties);
    }

    public GraphBundle toBundle(String name) {
        int nodesCount = vertices.size();
        int classesCount = vertices.stream().collect(Collectors.toSet()).size();
        GeneratorPropertiesPOJO properties = new GeneratorPropertiesPOJO(1, nodesCount, classesCount, 0, 0);
        return new GraphBundle(name, Collections.singletonList(this), properties);
    }
}
