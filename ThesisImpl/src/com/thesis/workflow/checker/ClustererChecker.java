package com.thesis.workflow.checker;


import com.thesis.adapter.generator.GraphBundle;
import com.thesis.clusterer.Clusterer;
import com.thesis.graph.SimpleNodeData;
import jeigen.DenseMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClustererChecker extends Checker {
    private static final CheckerType type = CheckerType.CLUSTERER;

    private GraphBundle graphs;
    private Integer k;

    public ClustererChecker(GraphBundle graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public String getName() {
        return type.name() + " (k=" + k + ")" + graphs.getName();
    }

    @Override
    public CheckerType getType() {
        return type;
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected CheckerTestResultDTO roundErrors(DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
        Integer countErrors = 0;

        final Clusterer clusterer = new Clusterer(D);
        final HashMap<Integer, Integer> data = clusterer.predictClusterer(k);

        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                if (data.get(i).equals(data.get(j)) != simpleNodeData.get(i).getLabel().equals(simpleNodeData.get(j).getLabel())) {
                    countErrors += 1;
                }
            }
        }

        Integer total = (int) Math.round(Math.floor((double) (data.size()*data.size()-data.size())/2.0));
        return new CheckerTestResultDTO(total, countErrors, 0);
    }

    @Override
    public ClustererChecker clone() {
        return new ClustererChecker(graphs, k);
    }
}
