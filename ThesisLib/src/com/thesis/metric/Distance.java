package com.thesis.metric;

import jeigen.DenseMatrix;

public abstract class Distance {
    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getShortName();

    public abstract void setShortName(String shortName);

    public abstract Scale getScale();

    public abstract void setScale(Scale scale);

    public abstract DenseMatrix getD(DenseMatrix A, double t);

    public String toString() {
        return getShortName();
    }
}
