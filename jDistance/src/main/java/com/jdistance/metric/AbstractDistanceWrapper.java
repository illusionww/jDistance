package com.jdistance.metric;

public abstract class AbstractDistanceWrapper {
    private String name;
    private Scale scale;
    private boolean isKernel;

    public AbstractDistanceWrapper(String name, Scale scale, boolean isKernel) {
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

    public boolean isKernel() {
        return isKernel;
    }
}
