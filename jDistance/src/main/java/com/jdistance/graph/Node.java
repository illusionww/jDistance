package com.jdistance.graph;

import com.jdistance.utils.Cloneable;

public class Node implements Comparable<Node>, Cloneable<Node> {
    String name;
    String label;

    public Node(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node that = (Node) o;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int compareTo(Node o) {
        if (this.getLabel() != null) {
            return this.getLabel().compareTo(o.getLabel());
        } else return 0;
    }

    @Override
    public Node clone() {
        return new Node(this.name, this.label);
    }
}
