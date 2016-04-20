package com.jdistance.distance;

import jeigen.DenseMatrix;

public class DistanceWrapper extends AbstractMeasureWrapper {
    private Distance distance;

    public DistanceWrapper(Distance distance) {
        super(distance.getName(), distance.getScale(), false);
        this.distance = distance;
    }

    public DistanceWrapper(String name, Distance distance) {
        super(name, distance.getScale(), false);
        this.distance = distance;
    }

    public DistanceWrapper(String name, Scale scale, Distance distance) {
        super(name, scale, false);
        this.distance = distance;
    }

    @Override
    public DenseMatrix calc(DenseMatrix A, double param) {
        return distance.getD(A, param).mul(-1);
    }
}
