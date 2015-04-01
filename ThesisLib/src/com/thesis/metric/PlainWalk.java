package com.thesis.metric;

import jeigen.DenseMatrix;

public class PlainWalk extends Distance {
    String name = "\"Plain\" Walk";
    String shortName = "pWalk";
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
        DenseMatrix H = db.getH0Walk(A, t);
        return db.getD(H);
    }
}
