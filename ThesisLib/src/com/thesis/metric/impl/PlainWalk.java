package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.DistancesBuilder;
import com.thesis.metric.Scale;
import jeigen.DenseMatrix;

public class PlainWalk extends Distance {
    String name = "pWalk";
    Scale scale = Scale.LINEAR;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String shortName) {
        this.name = shortName;
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
        t = db.rho(A, t);
        DenseMatrix H = db.getH0Walk(A, t);
        return db.getD(H);
    }
}
