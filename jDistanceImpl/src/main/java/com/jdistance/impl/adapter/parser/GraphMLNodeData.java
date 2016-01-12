package com.jdistance.impl.adapter.parser;

public class GraphMLNodeData implements Comparable<GraphMLNodeData> {
    private String nodeId;
    private String color;
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int compareTo(GraphMLNodeData o) {
        if (this.getColor() != null) {
            return this.getColor().compareTo(o.getColor());
        } else return 0;
    }
}
