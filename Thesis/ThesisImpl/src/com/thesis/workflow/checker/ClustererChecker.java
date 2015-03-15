package com.thesis.workflow.checker;


import com.thesis.clusterer.Clusterer;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.adapter.parser.graph.SimpleNodeData;
import org.jblas.DoubleMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClustererChecker extends Checker {
    private List<Graph> graphs;
    private Integer k;

    public ClustererChecker(List<Graph> graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public List<Graph> getGraphs() {
        return graphs;
    }

    @Override
    protected Integer[] roundErrors(DoubleMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
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

    @Override
    protected Double rate(Double countErrors, Double total, Integer coloredNodes) {
        return rate(countErrors, total);
    }

    private Double rate(Double countErrors, Double total){
        return 1 - countErrors / total;
    }
    
    @Override
    public ClustererChecker clone() {
        return new ClustererChecker(graphs, k);
    }
}
