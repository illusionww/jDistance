package com.thesis.adapter;

import com.jwetherell.algorithms.data_structures.Graph;
import com.jwetherell.algorithms.graph.Johnson;
import org.jblas.FloatMatrix;

import java.util.*;

public class JohnsonsAlgorithm {
    public static FloatMatrix getAllShortestPaths(FloatMatrix A) {
        int d = A.getColumns();
        FloatMatrix D = new FloatMatrix(d, d);
        List<Graph.Vertex<Float>> vertexes = getVertexes(A);
        List<Graph.Edge<Float>> edges = getEdges(A, vertexes);
        Graph graph = new Graph(vertexes, edges);
        Map<Graph.Vertex<Float>, Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>> result = Johnson.getAllPairsShortestPaths(graph);
        result.values().forEach(i -> i.values().forEach(j -> j.forEach(k -> {
            int from = Math.round(k.getFromVertex().getValue());
            int to = Math.round(k.getToVertex().getValue());
            D.put(from, to, k.getCost());
        })));
        return D;
    }

    public static List<Graph.Vertex<Float>> getVertexes(FloatMatrix A) {
        int d = A.getColumns();
        List<Graph.Vertex<Float>> list = new ArrayList<>();
        for (int i = 0; i < d; d++) {
            list.add(new Graph.Vertex<>((float)i));
        }
        return list;
    }

    public static List<Graph.Edge<Float>> getEdges(FloatMatrix A, List<Graph.Vertex<Float>> vertexes) {
        int d = A.getColumns();
        float[][] a = A.toArray2();
        List<Graph.Edge<Float>> list = new ArrayList<>();
        for (int i = 0; i < d; d++) {
            for (int j = 0; j < d; j++) {
                list.add(new Graph.Edge<>(a[i][j], vertexes.get(i), vertexes.get(j)));
            }
        }
        return list;
    }
}
