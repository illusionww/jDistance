package com.keithschwarz.johnsons;

import org.jblas.DoubleMatrix;

public class JohnsonsAlgorithm {
    public static DoubleMatrix getAllShortestPaths(DoubleMatrix A) {
        int d = A.columns;
        DirectedGraph<Integer> graph = new DirectedGraph<>();
        for (int i = 0; i < d; i++) {
            graph.addNode(i);
        }
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                if (A.get(i, j) > 0) {
                    graph.addEdge(i, j, 1.0/A.get(i, j));
                }
            }
        }

        DirectedGraph<Integer> out = Johnson.shortestPaths(graph);

        DoubleMatrix D = new DoubleMatrix(d, d);
        for (int i = 0; i < d; i++) {
            int from = i;
            out.edgesFrom(from).forEach((to, cost) -> D.put(from, to, cost.floatValue()));
        }

        return D;
    }
}
