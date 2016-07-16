package com.jdistance.graph;

import java.io.Serializable;

public class Node implements Comparable<Node>, Serializable {
    private int id;
    private int label;

    public Node(int id, int label) {
        this.id = id;
        this.label = label;
    }

    public int getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        return id == ((Node) o).id;
    }
}
