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
    private Integer k;
    private Double p;
    private Double x;


    public KNearestNeighborsGridSearch(GraphBundle graphs, Integer k, Double p) {
        super(graphs);
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
        super(graphs);
        this.k = k;
        this.p = p;
        this.x = x;
    }

    @Override
    public String getName() {
        return "Classifier: k=" + k + ", p=" + p + ", x=" + x + "; " + graphs.getName();
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D) {
        final List<Node> nodes = graph.getNodes();
        Integer countErrors = 0;

        final KNearestNeighbors classifier = new KNearestNeighbors(D, nodes);
        ArrayList<Node> data = classifier.predictLabel(k, p, x);

        for (int q = 0; q < data.size(); ++q) {
            Node original = nodes.get(q);
            Node calculated = data.get(q);
            if (original.getId().equals(calculated.getId()) && !original.getLabel().equals(calculated.getLabel())) {
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
