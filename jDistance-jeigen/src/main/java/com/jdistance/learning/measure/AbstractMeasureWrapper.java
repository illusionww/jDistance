package com.jdistance.learning.measure;

import jeigen.DenseMatrix;

import java.io.Serializable;

public abstract class AbstractMeasureWrapper implements Serializable {
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

    public void setName(String name) {
        this.name = name;
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

    public abstract DenseMatrix calc(DenseMatrix A, Double param);
}
