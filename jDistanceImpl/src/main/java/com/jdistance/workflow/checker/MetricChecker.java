package com.jdistance.workflow.checker;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.graph.Graph;
import com.jdistance.graph.SimpleNodeData;
import jeigen.DenseMatrix;

import java.util.ArrayList;

public class MetricChecker extends Checker {
    private static final CheckerType type = CheckerType.CLUSTERER;

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
        double sum = 0d;

        for (int i = 0; i < D.cols; i++) {
            for (int j = i + 1; j < D.rows; j++) {
                sum += D.get(i, j);
            }
        }

        double average = sum / (D.rows * (D.cols - 1) / 2);

        //среднеквадратичное отклонение
        double deviation = 0d;
        for (int i = 0; i < D.cols; i++) {
            for (int j = i + 1; j < D.rows; j++) {
                deviation += (D.get(i, j) - average) * (D.get(i, j) - average);
            }
        }

        //вычитаем среднее компонент и делим на среднеквадратичное отклонение
        if (D.rows != 0 && D.cols != 0 && deviation != 0) {
            deviation = (float) Math.sqrt(deviation / ((D.rows * (D.cols - 1) / 2) - 1));
            for (int i = 0; i < D.cols; i++) {
                for (int j = i + 1; j < D.rows; j++) {
                    D.set(i, j, (D.get(i, j) - average) / deviation);
                }
            }
        }

        //скалярное произведение (условно вытягиваем матрицу в вектор, 2 вектор имеет значения 0 если объекты в разных кластерах, 1 если в одном)
        double correlation = 0d;
        //создание второго вектора
        double x = 0d;
        double y = 0d;
        double[] vector2 = new double[D.cols * D.rows];
        for (int i = 0; i < D.cols; i++) {
            for (int j = 0; j < D.rows; j++) {
                if (graph.getSimpleNodeData().get(i).getLabel().equals(graph.getSimpleNodeData().get(j).getLabel())) {
                    vector2[i * D.cols + j] = 0d;
                } else vector2[i * D.cols + j] = 1d;
                if (i < j) {
                    correlation += D.get(i, j) * vector2[i * D.cols + j];
                    x += D.get(i, j) * D.get(i, j);
                    y += vector2[i * D.cols];
                }
            }
        }
        if (x != 0 && y != 0) {
            correlation = correlation / (Math.sqrt(x * y));
        }
        return new CheckerTestResultDTO(1.0d, correlation);
    }

    @Override
    public Checker clone() {
        return new MetricChecker(graphs, k);
    }
}
