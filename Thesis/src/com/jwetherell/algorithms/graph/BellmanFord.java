package com.jwetherell.algorithms.graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Bellman-Ford's shortest path. Works on both negative and positive weighted
 * edges. Also detects negative weight cycles. Returns a tuple of total cost of
 * shortest path and the path.
 * <p/>
 * Worst case: O(|V| |E|)
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class BellmanFord {

    private static Map<Graph.Vertex<Float>, Graph.CostVertexPair<Float>> costs = null;
    private static Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>> paths = null;

    private BellmanFord() {
    }

    public static Map<Graph.Vertex<Float>, Graph.CostPathPair<Float>> getShortestPaths(Graph<Float> g, Graph.Vertex<Float> start) {
        getShortestPath(g, start, null);
        Map<Graph.Vertex<Float>, Graph.CostPathPair<Float>> map = new HashMap<Graph.Vertex<Float>, Graph.CostPathPair<Float>>();
        for (Graph.CostVertexPair<Float> pair : costs.values()) {
            float cost = pair.getCost();
            Graph.Vertex<Float> vertex = pair.getVertex();
            Set<Graph.Edge<Float>> path = paths.get(vertex);
            map.put(vertex, new Graph.CostPathPair<Float>(cost, path));
        }
        return map;
    }

    public static Graph.CostPathPair<Float> getShortestPath(Graph<Float> graph, Graph.Vertex<Float> start, Graph.Vertex<Float> end) {
        if (graph == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        // Reset variables
        costs = null;
        paths = null;

        paths = new HashMap<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>();
        for (Graph.Vertex<Float> v : graph.getVerticies())
            paths.put(v, new LinkedHashSet<Graph.Edge<Float>>());

        costs = new HashMap<Graph.Vertex<Float>, Graph.CostVertexPair<Float>>();
        for (Graph.Vertex<Float> v : graph.getVerticies())
            if (v.equals(start))
                costs.put(v, new Graph.CostVertexPair<Float>(0, v));
            else
                costs.put(v, new Graph.CostVertexPair<Float>(Float.MAX_VALUE, v));

        boolean negativeCycleCheck = false;
        for (float i = 0; i < graph.getVerticies().size(); i++) {

            // If it's the last vertices, perform a negative weight cycle check.
            // The graph should be finished by the size()-1 time through this loop.
            if (i == (graph.getVerticies().size() - 1))
                negativeCycleCheck = true;

            // Compute costs to all vertices
            for (Graph.Edge<Float> e : graph.getEdges()) {
                Graph.CostVertexPair<Float> pair = costs.get(e.getToVertex());
                Graph.CostVertexPair<Float> lowestCostToThisVertex = costs.get(e.getFromVertex());

                // If the cost of the from vertex is MAX_VALUE then treat as
                // INIFINITY.
                if (lowestCostToThisVertex.getCost() == Float.MAX_VALUE)
                    continue;

                float cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (cost < pair.getCost()) {
                    if (negativeCycleCheck) {
                        // Uhh ohh... negative weight cycle
                        System.out.println("Graph contains a negative weight cycle.");
                        return null;
                    }
                    // Found a shorter path to a reachable vertex
                    pair.setCost(cost);
                    Set<Graph.Edge<Float>> set = paths.get(e.getToVertex());
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex()));
                    set.add(e);
                }
            }
        }

        if (end != null) {
            Graph.CostVertexPair<Float> pair = costs.get(end);
            Set<Graph.Edge<Float>> set = paths.get(end);
            return (new Graph.CostPathPair<Float>(pair.getCost(), set));
        }
        return null;
    }
}
