package com.jdistance.metric.impl;

import com.jdistance.metric.Distance;
import com.jdistance.metric.JeigenBuilder;
import com.jdistance.metric.Scale;
import jeigen.DenseMatrix;

public class FreeEnergy extends Distance {
    String name = "FE";
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
    public DenseMatrix getD(DenseMatrix A, double beta) {
        JeigenBuilder db = new JeigenBuilder();
        return db.getDFreeEnergy(A, beta);
    }
}
