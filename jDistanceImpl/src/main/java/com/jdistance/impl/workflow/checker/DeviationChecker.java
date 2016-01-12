package com.jdistance.impl.workflow.checker;

import com.jdistance.graph.Graph;
import com.jdistance.graph.NodeData;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.metric.Metric;
import com.jdistance.utils.NodesDistanceDTO;
import com.jdistance.utils.MatrixUtils;
import jeigen.DenseMatrix;

import java.util.*;

public class DeviationChecker extends Checker {
    private GraphBundle graphs;
    private Map<Graph, NodesDistanceDTO> bestBySP;


    public DeviationChecker(GraphBundle graphs) {
        this.graphs = graphs;
        this.bestBySP = new HashMap<>();

        graphs.getGraphs().forEach(graph -> {
            DenseMatrix A = graph.getSparseMatrix();
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
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, ArrayList<NodeData> nodeData) {
        NodesDistanceDTO nodesDistanceSP = bestBySP.get(graph);
        double[][] arrD = MatrixUtils.toArray2(D);
        Double nodesDistance = arrD[nodesDistanceSP.getFirstNodeIdx()][nodesDistanceSP.getSecondNodeIdx()];
        Double sum = Arrays.stream(arrD).flatMapToDouble(Arrays::stream).sum();
        Double avg = sum / (arrD.length * (arrD.length - 1));
        return new CheckerTestResultDTO(avg, nodesDistance);
    }

    @Override
    protected Double rate(List<CheckerTestResultDTO> results) {
        Double sum = 0.0;
        for (CheckerTestResultDTO result : results) {
            Double total = result.getTotal();
            Double countErrors = result.getCountErrors();
            sum += countErrors / total;
        }
        return sum / (double) results.size();
    }

    @Override
    public DeviationChecker clone() {
        return new DeviationChecker(graphs);
    }
}
