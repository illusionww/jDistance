package com.thesis.workflow.checker;

import com.thesis.classifier.Classifier;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.adapter.parser.graph.SimpleNodeData;

import java.util.ArrayList;
import java.util.List;

public class ClassifierChecker extends Checker {
    List<Graph> graphs;
    Integer k;
    Double p;

    public ClassifierChecker(List<Graph> graphs, Integer k, Double p) {
        this.graphs = graphs;
        this.k = k;
        this.p = p;
    }

    @Override
    public synchronized List<Graph> getGraphs() {
        return graphs;
    }

    @Override
    protected synchronized Integer[] roundErrors(double[][] D, ArrayList<SimpleNodeData> simpleNodeData) {
        Integer countErrors = 0;

        final Classifier classifier = new Classifier(D, simpleNodeData);
        ArrayList<SimpleNodeData> data = classifier.predictLabel(k, p);

        for (int q = 0; q < data.size(); ++q) {
            SimpleNodeData original = simpleNodeData.get(q);
            SimpleNodeData calculated = data.get(q);
            if (original.getName().equals(calculated.getName()) && !original.getLabel().equals(calculated.getLabel())) {
                countErrors += 1;
            }
        }

        return new Integer[] {data.size(), countErrors};
    }

    @Override
    protected synchronized Double rate(Double countErrors, Double total) {
        return 1 - p - countErrors / ((1 - p) * total);
    }

    @Override
    public synchronized ClassifierChecker clone() {
        return new ClassifierChecker(graphs, k, p);
    }
}
