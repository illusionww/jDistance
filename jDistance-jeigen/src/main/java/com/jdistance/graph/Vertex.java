package com.jdistance.graph;

import java.io.Serializable;

public class Vertex implements Comparable<Vertex>, Serializable {
    private int id;
    private int label;

    public Vertex(int id, int label) {
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
    public int compareTo(Vertex o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        return id == ((Vertex) o).id;
    }
}
