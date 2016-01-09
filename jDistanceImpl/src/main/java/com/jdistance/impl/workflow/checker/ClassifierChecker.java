package com.jdistance.impl.workflow.checker;

import com.jdistance.learning.classifier.KNearestNeighbors;
import com.jdistance.graph.Graph;
import com.jdistance.graph.NodeData;
import com.jdistance.impl.adapter.generator.GraphBundle;
import jeigen.DenseMatrix;

import java.util.ArrayList;

public class ClassifierChecker extends Checker {
    private GraphBundle graphs;
    private Integer k;
    private Double p;
    private Double x;

    public ClassifierChecker(GraphBundle graphs, Integer k, Double p) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = 0.0;
    }

    public ClassifierChecker(GraphBundle graphs, Integer k, Double p, Double x) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = x;
    }

    @Override
    public String getName() {
        return "Classifier (k=" + k + ", p=" + p + ", x=" + x + ") " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, ArrayList<NodeData> nodeData) {
        Integer countErrors = 0;

        final KNearestNeighbors classifier = new KNearestNeighbors(D, nodeData);
        ArrayList<NodeData> data = classifier.predictLabel(k, p, x);

        for (int q = 0; q < data.size(); ++q) {
            NodeData original = nodeData.get(q);
            NodeData calculated = data.get(q);
            if (original.getName().equals(calculated.getName()) && !original.getLabel().equals(calculated.getLabel())) {
                countErrors += 1;
            }
        }

        return new CheckerTestResultDTO(data.size(), countErrors, classifier.getCountColoredNodes());
    }

    @Override
    public ClassifierChecker clone() {
        return new ClassifierChecker(graphs, k, p, x);
    }
}
