package com.jdistance.impl.workflow.gridsearch.classifier;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.learning.classifier.KNearestNeighbors;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

public class KNearestNeighborsGridSearch extends GridSearch {
    private GraphBundle graphs;
    private Integer k;
    private Double p;
    private Double x;


    public KNearestNeighborsGridSearch(GraphBundle graphs, Integer k, Double p) {
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
    public KNearestNeighborsGridSearch(GraphBundle graphs, Integer k, Double p, Double x) {
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
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
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

        double total = data.size() - classifier.getCountColoredNodes();
        return 1.0 - countErrors / total;
    }

    @Override
    public KNearestNeighborsGridSearch clone() {
        return new KNearestNeighborsGridSearch(graphs, k, p, x);
    }
}
