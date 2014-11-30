package com.jwetherell.algorithms.data_structures;

import com.thesis.Main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Graph. Could be directed or undirected depending on the TYPE enum. A graph is
 * an abstract representation of a set of objects where some pairs of the
 * objects are connected by links.
 * <p/>
 * http://en.wikipedia.org/wiki/Graph_(mathematics)
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Graph<T extends Comparable<T>> {

    private List<Vertex<T>> verticies = new ArrayList<Vertex<T>>();
    private List<Edge<T>> edges = new ArrayList<Edge<T>>();

    public enum TYPE {
        DIRECTED, UNDIRECTED
    }

    private TYPE type = TYPE.UNDIRECTED;

    public Graph() {
    }

    public Graph(TYPE type) {
        this();
        this.type = type;
    }

    public Graph(Graph<T> g) {
        // Deep copies

        type = g.getType();

        // Copy the vertices (which copies the edges)
        for (Vertex<T> v : g.getVerticies())
            this.verticies.add(new Vertex<T>(v));

        // Update the object references
        for (Vertex<T> v : this.verticies) {
            for (Edge<T> e : v.getEdges()) {
                Vertex<T> fromVertex = e.getFromVertex();
                Vertex<T> toVertex = e.getToVertex();
                int indexOfFrom = this.verticies.indexOf(fromVertex);
                e.from = this.verticies.get(indexOfFrom);
                int indexOfTo = this.verticies.indexOf(toVertex);
                e.to = this.verticies.get(indexOfTo);
                this.edges.add(e);
            }
        }
    }

    public Graph(Collection<Vertex<T>> verticies, Collection<Edge<T>> edges) {
        this(TYPE.UNDIRECTED, verticies, edges);
    }

    public Graph(TYPE type, Collection<Vertex<T>> verticies, Collection<Edge<T>> edges) {
        this(type);
        this.verticies.addAll(verticies);
        this.edges.addAll(edges);

        for (Edge<T> e : edges) {
            Vertex<T> from = e.from;
            Vertex<T> to = e.to;

            if (!this.verticies.contains(from) || !this.verticies.contains(to))
                continue;

            int index = this.verticies.indexOf(from);
            Vertex<T> fromVertex = this.verticies.get(index);
            index = this.verticies.indexOf(to);
            Vertex<T> toVertex = this.verticies.get(index);
            fromVertex.addEdge(e);
            if (this.type == TYPE.UNDIRECTED) {
                Edge<T> reciprical = new Edge<T>(e.cost, toVertex, fromVertex);
                toVertex.addEdge(reciprical);
                this.edges.add(reciprical);
            }
        }
    }

    public TYPE getType() {
        return type;
    }

    public List<Vertex<T>> getVerticies() {
        return verticies;
    }

    public List<Edge<T>> getEdges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Vertex<T> v : verticies) {
            builder.append(v.toString());
        }
        return builder.toString();
    }

    public static class Vertex<T extends Comparable<T>> implements Comparable<Vertex<T>> {

        private T value = null;
        private float weight = 0;
        private List<Edge<T>> edges = new ArrayList<Edge<T>>();

        public Vertex(T value) {
            this.value = value;
        }

        public Vertex(T value, float weight) {
            this(value);
            this.weight = weight;
        }

        public Vertex(Vertex<T> vertex) {
            this(vertex.value, vertex.weight);
            this.edges = new ArrayList<Edge<T>>();
            for (Edge<T> e : vertex.edges)
                this.edges.add(new Edge<T>(e));
        }

        public T getValue() {
            return value;
        }

        public float getWeight() {
            return weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public void addEdge(Edge<T> e) {
            edges.add(e);
        }

        public List<Edge<T>> getEdges() {
            return edges;
        }

        public boolean pathTo(Vertex<T> v) {
            for (Edge<T> e : edges) {
                if (e.to.equals(v))
                    return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int code = this.value.hashCode() + Math.round(1000 * this.weight);
            return 31 * code;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object v1) {
            if (!(v1 instanceof Vertex))
                return false;

            Vertex<T> v = (Vertex<T>) v1;

            boolean values = this.value.equals(v.value);
            return values && this.weight == v.weight;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Vertex<T> v) {
            if (this.value == null || v.value == null)
                return -1;
            return this.value.compareTo(v.value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("vertex:").append(" value=").append(value).append(" weight=").append(weight).append("\n");
            for (Edge<T> e : edges) {
                builder.append("\t").append(e.toString());
            }
            return builder.toString();
        }
    }

    public static class Edge<T extends Comparable<T>> implements Comparable<Edge<T>> {

        private Vertex<T> from = null;
        private Vertex<T> to = null;
        private float cost = 0;

        public Edge(float cost, Vertex<T> from, Vertex<T> to) {
            if (from == null || to == null)
                throw (new NullPointerException("Both 'to' and 'from' Verticies need to be non-NULL."));
            this.cost = cost;
            this.from = from;
            this.to = to;
        }

        public Edge(Edge<T> e) {
            this(e.cost, e.from, e.to);
        }

        public float getCost() {
            return cost;
        }

        public void setCost(float cost) {
            this.cost = cost;
        }

        public Vertex<T> getFromVertex() {
            return from;
        }

        public Vertex<T> getToVertex() {
            return to;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return 31 * (Math.round(1000 * this.cost) * (this.getFromVertex().value.hashCode() * this.getToVertex().value.hashCode()));
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object e1) {
            if (!(e1 instanceof Edge))
                return false;

            Edge<T> e = (Edge<T>) e1;

            boolean costs = this.cost == e.cost;
            if (!costs)
                return false;

            boolean froms = this.from.equals(e.from);
            if (!froms)
                return false;

            boolean tos = this.to.equals(e.to);
            return tos;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Edge<T> e) {
            if (this.cost < e.cost)
                return -1;
            if (this.cost > e.cost)
                return 1;
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "edge:" + " [" + from.value + "]" + " -> " + "[" + to.value + "]" + " = " + cost + "\n";
        }
    }

    public static class CostVertexPair<T extends Comparable<T>> implements Comparable<CostVertexPair<T>> {

        private float cost = Integer.MAX_VALUE;
        private Vertex<T> vertex = null;

        public CostVertexPair(float cost, Vertex<T> vertex) {
            if (vertex == null)
                throw (new NullPointerException("vertex cannot be NULL."));

            this.cost = cost;
            this.vertex = vertex;
        }

        public float getCost() {
            return cost;
        }

        public void setCost(float cost) {
            this.cost = cost;
        }

        public Vertex<T> getVertex() {
            return vertex;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return 31 * (Math.round(1000 * this.cost) * this.vertex.hashCode());
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object e1) {
            if (!(e1 instanceof CostVertexPair))
                return false;

            CostVertexPair pair = (CostVertexPair) e1;
            return this.cost == pair.cost && this.vertex.equals(pair);

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(CostVertexPair<T> p) {
            if (p == null)
                throw new NullPointerException("CostVertexPair 'p' must be non-NULL.");
            if (this.cost < p.cost)
                return -1;
            if (this.cost > p.cost)
                return 1;
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Vertex=" + vertex.getValue() + " cost=" + cost + "\n";
        }
    }

    public static class CostPathPair<T extends Comparable<T>> {

        private float cost = 0;
        private Set<Edge<T>> path = null;

        public CostPathPair(float cost, Set<Edge<T>> path) {
            if (path == null)
                throw (new NullPointerException("path cannot be NULL."));

            this.cost = cost;
            this.path = path;
        }

        public float getCost() {
            return cost;
        }

        public void setCost(float cost) {
            this.cost = cost;
        }

        public Set<Edge<T>> getPath() {
            return path;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int hash = Math.round(1000 * this.cost);
            for (Edge<T> e : path)
                hash *= e.cost;
            return 31 * hash;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CostPathPair))
                return false;

            CostPathPair pair = (CostPathPair) obj;
            if (this.cost != pair.cost)
                return false;

            Object[] e = pair.path.toArray();
            int i = 0;
            for (Edge<T> e1 : path) {
                Edge<T> e2 = (Edge<T>) e[i++];
                if (!e1.equals(e2))
                    return false;
            }

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Cost = ").append(cost).append("\n");
            for (Edge<T> e : path) {
                builder.append("\t").append(e);
            }
            return builder.toString();
        }
    }
}