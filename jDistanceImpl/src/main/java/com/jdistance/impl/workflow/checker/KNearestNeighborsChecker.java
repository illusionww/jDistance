package com.jdistance.impl.workflow.checker;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.learning.classifier.KNearestNeighbors;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

public class KNearestNeighborsChecker extends Checker {
    private GraphBundle graphs;
    private Integer k;
    private Double p;
    private Double x;


    public KNearestNeighborsChecker(GraphBundle graphs, Integer k, Double p) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = 0.0;
    }

    /**
     * @param k - количество ближайших соседей
     * @param p - доля заранее размеченных вершин
     * @param x - параметр, влияющий на вес соседей
     */
    public KNearestNeighborsChecker(GraphBundle graphs, Integer k, Double p, Double x) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = x;
    }

    @Override
    public String getName() {
        return "Classifier: k=" + k + ", p=" + p + ", x=" + x + "; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, List<Node> node) {
        Integer countErrors = 0;

        final KNearestNeighbors classifier = new KNearestNeighbors(D, node);
        ArrayList<Node> data = classifier.predictLabel(k, p, x);

        for (int q = 0; q < data.size(); ++q) {
            Node original = node.get(q);
            Node calculated = data.get(q);
            if (original.getName().equals(calculated.getName()) && !original.getLabel().equals(calculated.getLabel())) {
                countErrors += 1;
            }
        }

        return new CheckerTestResultDTO(data.size(), countErrors, classifier.getCountColoredNodes());
    }

    @Override
    public KNearestNeighborsChecker clone() {
        return new KNearestNeighborsChecker(graphs, k, p, x);
    }
}
