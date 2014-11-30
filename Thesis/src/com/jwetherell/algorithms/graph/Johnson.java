package com.jwetherell.algorithms.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jwetherell.algorithms.data_structures.Graph;

/**
 * Johnson's algorithm is a way to find the shortest paths between all pairs of
 * vertices in a sparse directed graph. It allows some of the edge weights to be
 * negative numbers, but no negative-weight cycles may exist.
 * <p/>
 * Worst case: O(V^2 log V + VE)
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Johnson {

    private Johnson() {
    }

    public static Map<Graph.Vertex<Float>, Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>> getAllPairsShortestPaths(Graph<Float> g) {
        if (g == null)
            throw (new NullPointerException("Graph must be non-NULL."));

        Map<Graph.Vertex<Float>, Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>> allShortestPaths = new HashMap<Graph.Vertex<Float>, Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>>();

        // First, a new node 'connector' is added to the graph, connected by zero-weight edges to each of the other nodes.
        Graph<Float> graph = new Graph<Float>(g);
        Graph.Vertex<Float> connector = new Graph.Vertex<Float>(Float.MAX_VALUE);
        graph.getVerticies().add(connector);

        // Add the connector Vertex to all edges.
        for (Graph.Vertex<Float> v : g.getVerticies()) {
            int indexOfV = graph.getVerticies().indexOf(v);
            Graph.Edge<Float> edge = new Graph.Edge<Float>(0, connector, graph.getVerticies().get(indexOfV));
            connector.addEdge(edge);
            graph.getEdges().add(edge);
        }

        // Second, the Bellman–Ford algorithm is used, starting from the new vertex 'connector', to find for each vertex v 
        // the minimum weight h(v) of a path from 'connector' to v. If this step detects a negative cycle, the algorithm is terminated.
        Map<Graph.Vertex<Float>, Graph.CostPathPair<Float>> costs = BellmanFord.getShortestPaths(graph, connector);
        if (costs == null) {
            System.out.println("Graph contains a negative weight cycle. Cannot compute shortest path.");
            return null;
        }

        // Next the edges of the original graph are reweighted using the values computed by the Bellman–Ford algorithm: an edge 
        // from u to v, having length w(u,v), is given the new length w(u,v) + h(u) − h(v).
        for (Graph.Edge<Float> e : graph.getEdges()) {
            float weight = e.getCost();
            Graph.Vertex<Float> u = e.getFromVertex();
            Graph.Vertex<Float> v = e.getToVertex();

            // Don't worry about the connector
            if (u.equals(connector) || v.equals(connector)) continue;

            // Adjust the costs
            float uCost = costs.get(u).getCost();
            float vCost = costs.get(v).getCost();
            float newWeight = weight + uCost - vCost;
            e.setCost(newWeight);
        }

        // Finally, 'connector' is removed, and Dijkstra's algorithm is used to find the shortest paths from each node s to every 
        // other vertex in the reweighted graph.
        float indexOfConnector = graph.getVerticies().indexOf(connector);
        graph.getVerticies().remove(indexOfConnector);
        for (Graph.Edge<Float> e : connector.getEdges()) {
            float indexOfConnectorEdge = graph.getEdges().indexOf(e);
            graph.getEdges().remove(indexOfConnectorEdge);
        }

        for (Graph.Vertex<Float> v : g.getVerticies()) {
            Map<Graph.Vertex<Float>, Graph.CostPathPair<Float>> costPaths = Dijkstra.getShortestPaths(graph, v);
            Map<Graph.Vertex<Float>, Set<Graph.Edge<Float>>> paths = new HashMap<Graph.Vertex<Float>, Set<Graph.Edge<Float>>>();
            for (Graph.Vertex<Float> v2 : costPaths.keySet()) {
                Graph.CostPathPair<Float> pair = costPaths.get(v2);
                paths.put(v2, pair.getPath());
            }
            allShortestPaths.put(v, paths);
        }

        return allShortestPaths;
    }
}
