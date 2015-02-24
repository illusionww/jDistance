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
    public List<Graph> getGraphs() {
        return graphs;
    }

    @Override
    protected Integer[] roundErrors(double[][] D, ArrayList<SimpleNodeData> simpleNodeData) {
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
        return new Integer[] {total, countErrors};
    }
}
