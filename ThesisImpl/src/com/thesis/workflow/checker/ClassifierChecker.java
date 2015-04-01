package com.thesis.workflow.checker;

import com.thesis.classifier.Classifier;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

public class ClassifierChecker extends Checker {
    List<Graph> graphs;
    Integer k;
    Double p;
    Double x;

    public ClassifierChecker(List<Graph> graphs, Integer k, Double p) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = 0.0;
    }

    public ClassifierChecker(List<Graph> graphs, Integer k, Double p, Double x) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
        this.x = x;
    }

    @Override
    public String getName() {
        return "classifier (k=" + k + ", p=" + p + ")";
    }

    @Override
    public List<Graph> getGraphs() {
        return graphs;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    protected Integer[] roundErrors(DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
        Integer countErrors = 0;

        final Classifier classifier = new Classifier(D, simpleNodeData);
        ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p, x);

        for (int q = 0; q < data.size(); ++q) {
            SimpleNodeData original = simpleNodeData.get(q);
            SimpleNodeData calculated = data.get(q);
            if (original.getName().equals(calculated.getName()) && !original.getLabel().equals(calculated.getLabel())) {
                countErrors += 1;
            }
        }

        return new Integer[]{data.size(), countErrors, classifier.getCountColoredNodes()};
    }

    @Override
    protected Double rate(Double countErrors, Double total, Integer coloredNodes) {
        return 1 - (double)coloredNodes/total - countErrors / (total - coloredNodes);    }

    @Override
    public ClassifierChecker clone() {
        return new ClassifierChecker(graphs, k, p, x);
    }
}
