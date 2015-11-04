package com.jdistance.metric.impl;

import com.jdistance.metric.Distance;
import com.jdistance.metric.JeigenBuilder;
import com.jdistance.metric.Scale;
import jeigen.DenseMatrix;

public class LogComm_D extends Distance {
    String name = "logComm Dummy";
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
        DenseMatrix H0 = db.getH0DummyCommunicability(A, t);
        DenseMatrix H = db.H0toH(H0);
        DenseMatrix D = db.getD(H);
        return db.sqrtD(D);
    }
}
