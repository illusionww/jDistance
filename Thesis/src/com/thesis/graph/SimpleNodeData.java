package com.thesis.graph;

public class SimpleNodeData implements Comparable<SimpleNodeData> {
    String name;
    String label; //color

    public SimpleNodeData(String name, String label) {
        this.name = name;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
        if (!(o instanceof SimpleNodeData)) {
            return false;
        }

        SimpleNodeData that = (SimpleNodeData) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int compareTo(SimpleNodeData o) {
        if (this.getLabel() != null) {
            return this.getLabel().compareTo(o.getLabel());
        } else return 0;
    }
}
