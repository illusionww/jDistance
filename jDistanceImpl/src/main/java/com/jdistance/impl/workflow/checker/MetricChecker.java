package com.jdistance.impl.workflow.checker;

import com.jdistance.graph.Graph;
import com.jdistance.graph.SimpleNodeData;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.util.StandardizeHelper;
import jeigen.DenseMatrix;

import java.util.ArrayList;

public class MetricChecker extends Checker {
    private static final CheckerType type = CheckerType.METRIC;

    private GraphBundle graphs;
    private Integer k;

    public MetricChecker(GraphBundle graphs, Integer k) {
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
    protected CheckerTestResultDTO roundErrors(Graph graph, DenseMatrix D, ArrayList<SimpleNodeData> simpleNodeData) {
        double[] vector1 = StandardizeHelper.standardize(D).getValues(); //вытягиваем матрицу в вектор

        double[][] class_match = new double[D.cols][D.rows]; // 1 если объекты в разных кластерах, 0 если в одном
        for (int c = 0; c < D.cols; c++) {
            for (int r = 0; r < D.rows; r++) {
                class_match[c][r] = graph.getSimpleNodeData().get(c).getLabel().equals(graph.getSimpleNodeData().get(r).getLabel())
                        ? 0d : 1d;
            }
        }
        DenseMatrix B = new DenseMatrix(class_match);
        double[] vector2 = StandardizeHelper.standardize(B).getValues();

        double cov = 0d;
        for (int i = 0; i < vector1.length; i++) {
            cov += vector1[i] * vector2[i]; // скалярное произведение и даст ковариацию
        }
        cov /= (double) D.cols * (D.rows - 1);
        return new CheckerTestResultDTO(1.0d, -cov + 1);
    }

    @Override
    public Checker clone() {
        return new MetricChecker(graphs, k);
    }
}
