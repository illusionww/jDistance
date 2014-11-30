package com.jwetherell.algorithms.graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Dijkstra's shortest path. Only works on non-negative path weights. Returns a
 * tuple of total cost of shortest path and the path.
 * <p/>
 * Worst case: O(|E| + |V| log |V|)
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Dijkstra {

    private static Map<Graph.Vertex<Float>, Graph.CostVertexPair<Float>> costs = null;
    private static Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>> paths = null;

    private Dijkstra() {
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
        Queue<Graph.CostVertexPair<Float>> unvisited = null;

        // Dijkstra's algorithm only works on positive cost graphs
        boolean hasNegativeEdge = checkForNegativeEdges(graph.getVerticies());
        if (hasNegativeEdge)
            throw (new IllegalArgumentException("Negative cost Edges are not allowed."));

        paths = new HashMap<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>();
        for (Graph.Vertex<Float> v : graph.getVerticies())
            paths.put(v, new LinkedHashSet<Graph.Edge<Float>>());

        costs = new HashMap<Graph.Vertex<Float>, Graph.CostVertexPair<Float>>();
        for (Graph.Vertex<Float> v : graph.getVerticies()) {
            if (v.equals(start))
                costs.put(v, new Graph.CostVertexPair<Float>(0, v));
            else
                costs.put(v, new Graph.CostVertexPair<Float>(Float.MAX_VALUE, v));
        }

        unvisited = new PriorityQueue<Graph.CostVertexPair<Float>>();
        unvisited.addAll(costs.values()); // Shallow copy which is O(n log n)

        Graph.Vertex<Float> vertex = start;
        while (true) {
            // Compute costs from current vertex to all reachable vertices which haven't been visited
            for (Graph.Edge<Float> e : vertex.getEdges()) {
                Graph.CostVertexPair<Float> pair = costs.get(e.getToVertex()); // O(log n)
                Graph.CostVertexPair<Float> lowestCostToThisVertex = costs.get(vertex); // O(log n)
                float cost = lowestCostToThisVertex.getCost() + e.getCost();
                if (pair.getCost() == Float.MAX_VALUE) {
                    // Haven't seen this vertex yet
                    pair.setCost(cost);

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(pair); // O(n)
                    unvisited.add(pair); // O(log n)

                    // Update the paths
                    Set<Graph.Edge<Float>> set = paths.get(e.getToVertex()); // O(log n)
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                } else if (cost < pair.getCost()) {
                    // Found a shorter path to a reachable vertex
                    pair.setCost(cost);

                    // Need to remove the pair and re-insert, so the priority queue keeps it's invariants
                    unvisited.remove(pair); // O(n)
                    unvisited.add(pair); // O(log n)

                    // Update the paths
                    Set<Graph.Edge<Float>> set = paths.get(e.getToVertex()); // O(log n)
                    set.clear();
                    set.addAll(paths.get(e.getFromVertex())); // O(log n)
                    set.add(e);
                }
            }

            // Termination conditions
            if (end != null && vertex.equals(end)) {
                // If we are looking for shortest path, we found it.
                break;
            } else if (unvisited.size() > 0) {
                // If there are other vertices to visit (which haven't been visited yet)
                Graph.CostVertexPair<Float> pair = unvisited.remove();  // O(log n)
                vertex = pair.getVertex();
                if (pair.getCost() == Float.MAX_VALUE) {
                    // If the only edge left to explore has MAX_VALUE then it
                    // cannot be reached from the starting vertex
                    break;
                }
            } else {
                // No more edges to explore, we are done.
                break;
            }
        }

        if (end != null) {
            Graph.CostVertexPair<Float> pair = costs.get(end);
            Set<Graph.Edge<Float>> set = paths.get(end);
            return (new Graph.CostPathPair<Float>(pair.getCost(), set));
        }
        return null;
    }

    private static boolean checkForNegativeEdges(List<Graph.Vertex<Float>> vertitices) {
        for (Graph.Vertex<Float> v : vertitices) {
            for (Graph.Edge<Float> e : v.getEdges()) {
                if (e.getCost() < 0)
                    return true;
            }
        }
        return false;
    }
}
