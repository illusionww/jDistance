package com.jdistance.metric;

public class NodesDistanceDTO {
    private int i, j;
    private double value;

    public NodesDistanceDTO(int i, int j, double value) {
        this.i = i;
        this.j = j;
        this.value = value;
    }

    public void setValue(int i, int j, double value) {
        this.i = i;
        this.j = j;
        this.value = value;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public double getValue() {
        return value;
    }
}