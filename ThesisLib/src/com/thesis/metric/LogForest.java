package com.thesis.metric;

import jeigen.DenseMatrix;

public class LogForest extends Distance {
    String name = "Logarithmic Forest";
    String shortName = "logFor";
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
        DenseMatrix L = db.getL(A);
        DenseMatrix H0 = db.getH0Forest(L, t);
        DenseMatrix H = db.H0toH(H0);
        return db.getD(H);
    }
}
