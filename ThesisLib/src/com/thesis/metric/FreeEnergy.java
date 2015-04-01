package com.thesis.metric;

import jeigen.DenseMatrix;

public class FreeEnergy extends Distance {
    String name = "Helmholtz Free Energy Distances";
    String shortName = "FE";
    Scale scale = Scale.DEFAULT;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public Scale getScale() {
        return scale;
    }

    @Override
    public void setScale(Scale scale) {
        this.scale = scale;
    }

    @Override
    public DenseMatrix getD(DenseMatrix A, double beta) {
        DistancesBuilder db = new DistancesBuilder();
        return db.getDFreeEnergy(A, beta);
    }
}
