package com.thesis.metric;

import jeigen.DenseMatrix;

public class Communicability extends Distance {
    String name = "Communicability Distances";
    String shortName = "Comm";
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
    public DenseMatrix getD(DenseMatrix A, double t) {
        DistancesBuilder db = new DistancesBuilder();
        DenseMatrix H = db.getH0Communicability(A, t);
        DenseMatrix D = db.getD(H);
        return db.sqrtD(D);
    }
}
