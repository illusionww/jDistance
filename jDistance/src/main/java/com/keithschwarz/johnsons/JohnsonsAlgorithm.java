package com.keithschwarz.johnsons;

import jeigen.DenseMatrix;

public class JohnsonsAlgorithm {
    public static DenseMatrix getAllShortestPaths(DenseMatrix A) {
        int d = A.cols;
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

        DenseMatrix D = new DenseMatrix(d, d);
        for (int i = 0; i < d; i++) {
            int from = i;
            out.edgesFrom(from).forEach((to, cost) -> D.set(from, to, cost.floatValue()));
        }

        return D;
    }
}
