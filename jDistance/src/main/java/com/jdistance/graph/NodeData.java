package com.jdistance.graph;

import com.jdistance.utils.Cloneable;

public class NodeData implements Comparable<NodeData>, Cloneable<NodeData> {
    String name;
    String label;

    public NodeData(String name, String label) {
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
        if (!(o instanceof NodeData)) {
            return false;
        }
        NodeData that = (NodeData) o;
        return label != null ? label.equals(that.label) : that.label == null &&
                name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int compareTo(NodeData o) {
        if (this.getLabel() != null) {
            return this.getLabel().compareTo(o.getLabel());
        } else return 0;
    }

    @Override
    public NodeData clone() {
        return new NodeData(this.name, this.label);
    }
}
