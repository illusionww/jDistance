package com.jdistance.utils;

public class NodesDistanceDTO {
    private int firstNodeIdx, secondNodeIdx;
    private double distance;

    public NodesDistanceDTO(int firstNodeIdx, int secondNodeIdx, double distance) {
        this.firstNodeIdx = firstNodeIdx;
        this.secondNodeIdx = secondNodeIdx;
        this.distance = distance;
    }

    public int getFirstNodeIdx() {
        return firstNodeIdx;
    }

    public int getSecondNodeIdx() {
        return secondNodeIdx;
    }

    public double getDistance() {
        return distance;
    }
}