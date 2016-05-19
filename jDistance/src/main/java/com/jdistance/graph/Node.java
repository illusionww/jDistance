package com.jdistance.graph;

import java.io.Serializable;
import java.util.Objects;

public class Node implements Comparable<Node>, Serializable {
    private Integer id;
    private String label;

    public Node(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(Node o) {
        if (this.getLabel() != null) {
            return this.getLabel().compareTo(o.getLabel());
        } else return 0;
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
        return Objects.equals(id, that.id);
    }
}
