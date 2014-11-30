package com.thesis.algorithm.johnsons;

import org.jblas.FloatMatrix;

public class JohnsonsAlgorithm {
    public static FloatMatrix getAllShortestPaths(FloatMatrix A) {
        int d = A.getColumns();
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

        FloatMatrix D = new FloatMatrix(d, d);
        for (int i = 0; i < d; i++) {
            int from = i;
            out.edgesFrom(from).forEach((to, cost) -> {
                D.put(from, to, cost.floatValue());
            });
        }

        return D;
    }
}
