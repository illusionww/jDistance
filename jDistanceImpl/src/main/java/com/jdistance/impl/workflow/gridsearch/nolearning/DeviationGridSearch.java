package com.jdistance.impl.workflow.gridsearch.nolearning;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.metric.Metric;
import com.jdistance.utils.MatrixUtils;
import com.jdistance.utils.NodesDistanceDTO;
import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviationGridSearch extends GridSearch {
    private GraphBundle graphs;
    private Map<Graph, NodesDistanceDTO> bestBySP;

    public DeviationGridSearch(GraphBundle graphs) {
        this.graphs = graphs;
        this.bestBySP = new HashMap<>();

        graphs.getGraphs().forEach(graph -> {
            DenseMatrix A = graph.getA();
            this.bestBySP.put(graph, Metric.SP_CT.getBiggestDistance(A, 0.0));
        });
    }

    @Override
    public String getName() {
        return "Deviation; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
        NodesDistanceDTO nodesDistanceSP = bestBySP.get(graph);
        double[][] arrD = MatrixUtils.toArray2(D);
        double nodesDistance = arrD[nodesDistanceSP.getFirstNodeIdx()][nodesDistanceSP.getSecondNodeIdx()];
        double sum = Arrays.stream(arrD).flatMapToDouble(Arrays::stream).sum();
        double avg = sum / (arrD.length * (arrD.length - 1));
        return nodesDistance / avg;
    }

    @Override
    public DeviationGridSearch clone() {
        return new DeviationGridSearch(graphs);
    }
}
