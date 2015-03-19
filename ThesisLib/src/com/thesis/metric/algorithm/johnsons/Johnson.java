package com.thesis.metric.algorithm.johnsons;

/*****************************************************************************
 * File: Johnson.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Johnson's all-pairs shortest paths algorithm.  This
 * algorithm is remarkable in that it combines two single-source shortest path
 * algrithms - Dijkstra's algorithm and the Bellman-Ford algorithm - into a
 * single algorithm for all-pairs shortest paths that ends up being more
 * efficient than the Floyd-Warshall all-pairs shorest paths algorithm when
 * run on sparse graphs.
 *
 * The idea behind the algorithm is as follows.  If a graph contains no
 * negative edges, then we can compute all-pairs shortest paths in
 * O(mn + n^2 log n) by calling Dijkstra's algorithm (O(m + n lg n)) once for
 * each of the n nodes in the graph.  However, in general, we can't make this
 * assumption about edge weights.  The idea behind Johnson's algorithm is to
 * enforce that all the edge costs in the graph are nonnegative by modifying
 * the costs of the nodes in the graph.  In particular, it computes a
 * potential function h(v) for each node in the graph, and then creates a new
 * edge cost for each edge:
 *
 *                        l'(u, v) = l(u, v) - h(v) + h(u)
 *
 * The key insight is that for any path P in the graph with updated edge
 * costs, we have that
 *
 * l'(P) =     SUM    l'(u, v) =     SUM     (l(u, v) - h(v) + h(u))
 *        (u, v) in P            (u, v) in P
 *
 *       = (    SUM       l(u, v)) - h(v_k) + h(v_0)   (path from v_0 to v_k)
 *          (u, v) in P
 *
 *       = l(P) - h(v_k) + h(v_0)
 *
 * The rationale behind the transition from the first line to the second is
 * that the sum of h(u) - h(v) telescopes, since the h(v) of one term in the
 * sum is the h(u) term in the next.  Consequently, we have that the cost of
 * any path in the new graph is given by the cost of the path in the old graph
 * plus some constant term that depends on the choice of h.  Consequently, any
 * shortest path in the new graph is also a shortest path in the old graph,
 * though it may have higher weight.
 *
 * The key insight behind Johnson's algorithm is how to compute a choice of h
 * such that l'(u, v) is nonnegative for all (u, v) \in E.  For this, the
 * algorithm uses a remarkable trick.  It begins by constructing a new,
 * augmented graph G' formed by taking the input graph G, then adding a new
 * source node s with a length-zero edge to each node in G.  The value of h(v)
 * is then the cost of the shortest path in the graph from s to v.  Since the
 * graph may contain negative edges, we can find these paths most efficiently
 * using the Bellman-Ford algorithm starting from s.  Assuming the input graph
 * has no negative cycles, the new graph does not contain negative cycles
 * either, because all the edges from s are directed into the old graph.
 *
 * The reason that this trick works is that we have that for any nodes u and v
 * with an edge from u to v between them, that
 *
 *                           h(v) <= h(u) + l(u, v)
 *
 * or, equivalently
 *                           h(v) - h(u) <= l(u, v)
 *
 * This holds because one possible shortest path from s to v is to take the
 * shortest path from s to u, then to follow the edge from u to v.  From this,
 * we get that
 *
 *          l'(u, v) = l(u, v) - h(v) + h(u) >= l(u, v) - l(u, v) = 0
 *
 * All the edge weights are now nonnegative, and Dijkstra's algorithm can be
 * used to compute shortest paths.
 *
 * Let's finally consider the runtime of this algorithm.  The new graph G' can
 * be constructed in time O(n + m) by simply cloning the graph and adding some
 * extra edges (O(n) of them) to it.  The resulting graph has O(n) nodes and
 * O(m + n) = O(m) edges.  Running Bellman-Ford here takes time O(mn), and
 * the final run of Dijkstra's algorithm then takes O(mn + n^2 lg n) for a net
 * runtime of O(mn + n^2 lg n).  In the worst case when m = Theta(n^2) this
 * runtime is O(n^3), matching the bound set by Floyd-Warshall, but if m is
 * low (say, O(n lg n)) the runtime is O(n^2 lg n), asymptotically faster than
 * Floyd-Warshall.
 *
 * This code relies on several other pieces of code from the Archive of
 * Interesting Code.  In particular, it needs
 *
 * Dijkstra's Algorithm:
 *      http://www.keithschwarz.com/interesting/code/?dir=dijkstra
 * Bellman-Ford Algorithm:
 *      http://www.keithschwarz.com/interesting/code/?dir=bellman-ford
 * Fibonacci Heap:
 *      http://www.keithschwarz.com/interesting/code/?dir=fibonacci-heap
 */

import java.util.*; // For HashMap

final class Johnson {
    /* An object to use as the source node which cannot normally appear in an
     * input graph.
     */
    private static final Object SOURCE_NODE = new Object();

    /**
     * Given a directed, weighted graph G, runs Johnson's algorithm on that
     * graph and produces a new graph with an edge (i, j) between each pair of
     * nodes whose cost is the cost of the shortest path from i to j in the
     * input graph.
     *
     * @param graph The graph on which to run Johnson's algorithm.
     * @return A graph containing the all-pairs shortest paths of the input
     *         graph.
     */
    public static <T> DirectedGraph<T> shortestPaths(DirectedGraph<T> graph) {
        /* Construct the augmented graph G' that will be fed into the Bellman-
         * Ford step by copying the graph and adding an extra node.  Because
         * the source node is of type Object (because we can't necessarily
         * create an element of type T that isn't already in the graph), this
         * graph will store plain old Objects.
         */
        DirectedGraph<Object> augmentedGraph = constructAugmentedGraph(graph);

        /* Compute the single-source shortest path from the source node to
         * each other node in the graph to get the potential function h.
         */
        Map<Object, Double> potential = BellmanFord.shortestPaths(augmentedGraph, SOURCE_NODE);

        /* Now, reweight the edges of the input graph by adjusting edge
         * weights based on the potential.
         */
        DirectedGraph<T> reweightedGraph = reweightGraph(graph, potential);

        /* Our return value is a graph with the all-pairs shortest paths
         * values for edges.  We'll begin by initializing it so that it has a
         * copy of each node in the source graph.
         */
        DirectedGraph<T> result = new DirectedGraph<T>();
        for (T node: graph)
            result.addNode(node);

        /* Now, run Dijkstra's algorithm over every node in the updated graph
         * to get the transformed shortest path costs.
         */
        for (T node: graph) {
            Map<T, Double> costs = Dijkstra.shortestPaths(reweightedGraph, node);

            /* We now have the shortest path costs from this node to all other
             * nodes, but the costs are using the new edges rather than the
             * old.  In particular, we have that
             *
             *                  C'(u, v) = C(u, v) + h(u) - h(v)
             *
             * When recording these costs in the new graph, we'll therefore
             * add in h(v) - h(u).
             */
            for (Map.Entry<T, Double> path: costs.entrySet())
                result.addEdge(node, path.getKey(),
                        path.getValue() + potential.get(path.getKey()) - potential.get(node));
        }

        /* Hand back the resulting graph. */
        return result;
    }

    /**
     * Utility function which, given a directed graph, constructs the
     * augmented graph G' by adding an extra source node.
     *
     * @param graph The graph to augment.
     * @return An augmented version of that graph.
     */
    private static <T> DirectedGraph<Object> constructAugmentedGraph(DirectedGraph<T> graph) {
        DirectedGraph<Object> result = new DirectedGraph<Object>();

        /* Copy over the nodes. */
        for (Object node: graph)
            result.addNode(node);

        /* Copy over the edges. */
        for (T node: graph)
            for (Map.Entry<T, Double> edge: graph.edgesFrom(node).entrySet())
                result.addEdge(node, edge.getKey(), edge.getValue());

        /* Add the new node to the graph. */
        result.addNode(SOURCE_NODE);

        /* Connect it to each other node with an edge of cost zero. */
        for (Object node: graph)
            result.addEdge(SOURCE_NODE, node, 0.0);

        return result;
    }

    /**
     * Utility function which, given a graph and a potential function on that
     * graph (encoded as a map from nodes to their potentials), produces a new
     * graph whose edges are weighted by the potential.
     *
     * @param graph The graph to reweight.
     * @param potential The potential function to apply.
     * @return A reweighted version of the graph.
     */
    private static <T> DirectedGraph<T> reweightGraph(DirectedGraph<T> graph,
                                                      Map<Object, Double> potential) {
        /* Begin by copying over all the nodes from the old graph. */
        DirectedGraph<T> result = new DirectedGraph<T>();
        for (T node: graph)
            result.addNode(node);

        /* Now, copy over the edge with new weights; in particular, by using
         * l'(u, v) = l(u, v) - l(v) + l(u).
         */
        for (T node: graph)
            for (Map.Entry<T, Double> edge: graph.edgesFrom(node).entrySet())
                result.addEdge(node, edge.getKey(),
                        edge.getValue() + potential.get(node) - potential.get(edge.getKey()));

        return result;
    }
}