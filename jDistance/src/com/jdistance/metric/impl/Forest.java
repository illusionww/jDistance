package com.jdistance.metric.impl;

import com.jdistance.metric.Distance;
import com.jdistance.metric.JeigenBuilder;
import com.jdistance.metric.Scale;
import jeigen.DenseMatrix;

public class Forest extends Distance {
    String name = "For";
    Scale scale = Scale.FRACTION;

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
        DenseMatrix H = db.getH0Forest(L, t);
        return db.getD(H);
    }
}
