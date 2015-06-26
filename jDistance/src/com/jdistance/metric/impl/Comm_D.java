package com.jdistance.metric.impl;

import com.jdistance.metric.Distance;
import com.jdistance.metric.Scale;
import com.jdistance.metric.JeigenBuilder;
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
