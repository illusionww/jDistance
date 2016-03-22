package com.jdistance.impl.workflow.gridsearch.nolearning;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.Node;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.util.StandardizeHelper;
import jeigen.DenseMatrix;

import java.util.List;

/**
 *  Берем вектор всех расстояний (с оптим. параметром семейства) - результат вытягивания матрицы в вектор.
 *  Стандартизуем его, вычтя среднее компонент и поделив на ср.-кв. отклонение.
 *  Для сравнения берем вектор с компонентой, равной 0, когда пара вершин входит в один кластер и 1, если в разные.
 *  В качестве грубоватого показателя связи берем корреляцию этого вектора и вектора расстояний.
 *  (Для него и стандартизация не нужна - он инвариантен к линейным преобразованиям).
 */
public class MetricGridSearch extends GridSearch {
    private GraphBundle graphs;
    private Integer k;

    public MetricGridSearch(GraphBundle graphs, Integer k) {
        this.graphs = graphs;
        this.k = k;
    }

    @Override
    public String getName() {
        return "Metric: k=" + k + "; " + graphs.getName();
    }

    @Override
    public GraphBundle getGraphBundle() {
        return graphs;
    }

    @Override
    protected double roundScore(Graph graph, DenseMatrix D, List<Node> node) {
        double[] vector1 = StandardizeHelper.standardize(D).getValues(); //вытягиваем матрицу в вектор

        double[][] class_match = new double[D.cols][D.rows]; // 1 если объекты в разных кластерах, 0 если в одном
        for (int c = 0; c < D.cols; c++) {
            for (int r = 0; r < D.rows; r++) {
                class_match[c][r] = graph.getNodes().get(c).getLabel().equals(graph.getNodes().get(r).getLabel())
                        ? 0d : 1d;
            }
        }
        DenseMatrix B = new DenseMatrix(class_match);
        double[] vector2 = StandardizeHelper.standardize(B).getValues();

        double cov = 0d;
        for (int i = 0; i < vector1.length; i++) {
            cov += vector1[i] * vector2[i]; // скалярное произведение и даст ковариацию
        }
        return 1.0 - cov / (double) D.cols * (D.rows - 1);
    }

    @Override
    public GridSearch clone() {
        return new MetricGridSearch(graphs, k);
    }
}
