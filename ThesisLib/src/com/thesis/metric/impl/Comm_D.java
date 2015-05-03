package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.metric.builder.JeigenBuilder;
import jeigen.DenseMatrix;

public class Comm_D extends Distance {
    String name = "Comm Dummy";
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
        DenseMatrix H = db.getH0DummyCommunicability(A, t);
        DenseMatrix D = db.getD(H);
        return db.sqrtD(D);
    }
}
