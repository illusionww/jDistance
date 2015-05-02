package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.builder.JeigenBuilder;
import com.thesis.metric.Scale;
import jeigen.DenseMatrix;

public class LogForest extends Distance {
    String name = "logFor";
    Scale scale = Scale.FRACTION_BETA;

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
        JeigenBuilder db = new JeigenBuilder();
        DenseMatrix L = db.getL(A);
        DenseMatrix H0 = db.getH0Forest(L, t);
        DenseMatrix H = db.H0toH(H0);
        return db.getD(H);
    }
}
