package com.thesis.metric;

import jeigen.DenseMatrix;

public class Walk extends Distance {
    String name = "Walk";
    String shortName = "Walk";
    Scale scale = Scale.LINEAR;

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
    public DenseMatrix getD(DenseMatrix A, double alpha) {
        DistancesBuilder db = new DistancesBuilder();
        double t = db.alphaToT(A, alpha);
        DenseMatrix H0 = db.getH0Walk(A, t);
        DenseMatrix H = db.H0toH(H0);
        return db.getD(H);
    }
}
