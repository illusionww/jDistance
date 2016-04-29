package com.jdistance.distance;

import jeigen.DenseMatrix;

public abstract class AbstractMeasureWrapper {
    private String name;
    private Scale scale;
    private boolean isKernel;

    public AbstractMeasureWrapper(String name, Scale scale, boolean isKernel) {
        this.name = name;
        this.scale = scale;
        this.isKernel = isKernel;
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public boolean isKernel() {
        return isKernel;
    }

    public abstract DenseMatrix calc(DenseMatrix A, double param);
}