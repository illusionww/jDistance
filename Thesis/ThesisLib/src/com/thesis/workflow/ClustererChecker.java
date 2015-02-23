package com.thesis.workflow;


import com.thesis.clusterer.Clusterer;
import com.thesis.graph.Graph;
import com.thesis.graph.SimpleNodeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClustererChecker extends Checker {
    List<Graph> graphs;
    Integer k;

    public ClustererChecker(List<Graph> graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    protected Integer roundErrors(double[][] D, ArrayList<SimpleNodeData> simpleNodeData) {
        Integer countErrors = 0;

        final Clusterer clusterer = new Clusterer(D);
        final HashMap<Integer, Integer> data = clusterer.predictClusterer(k);

        for (int i = 0; i < data.size(); ++i) {
            for (int j = i + 1; j < data.size(); ++j) {
                if (data.get(j).equals(data.get(k)) == simpleNodeData.get(j).getLabel().equals(simpleNodeData.get(k).getLabel())) {
                    countErrors += 1;
                }
            }
        }

        return countErrors;
    }
}
