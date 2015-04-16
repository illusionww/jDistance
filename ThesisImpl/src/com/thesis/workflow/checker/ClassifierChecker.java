package com.thesis.workflow.checker;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.classifier.Classifier;
import com.thesis.graph.SimpleNodeData;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

public class ClassifierChecker extends Checker {
    private static final CheckerType type = CheckerType.CLASSIFIER;

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
        return type.name() + " (k=" + k + ", p=" + p + ", x=" + x + ") " + graphs.getName();
    }

    @Override
    public CheckerType getType() {
        return type;
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Override
    protected CheckerTestResultDTO roundErrors(DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
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

        return new CheckerTestResultDTO(data.size(), countErrors, classifier.getCountColoredNodes());
    }

    @Override
    protected Double rate(List<CheckerTestResultDTO> results) {
        Double sum = results.stream().mapToDouble(i -> 1 - (double)i.getColoredNodes()/i.getTotal() - i.getCountErrors()/ (i.getTotal() - i.getColoredNodes())).sum();
        return sum / (double) results.size();
    }

    @Override
    public ClassifierChecker clone() {
        return new ClassifierChecker(graphs, k, p, x);
    }
}
