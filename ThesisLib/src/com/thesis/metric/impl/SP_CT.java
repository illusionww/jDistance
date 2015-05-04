package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.JeigenBuilder;
import com.thesis.metric.Scale;
import jeigen.DenseMatrix;

public class SP_CT extends Distance {
    String name = "SP-CT";
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
    public DenseMatrix getD(DenseMatrix A, double lambda) {
        if (lambda < 0.0 || lambda > 1.0) {
            throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
        }

        JeigenBuilder db = new JeigenBuilder();
        DenseMatrix L = db.getL(A);
        DenseMatrix Ds = db.getDShortestPath(A);
        DenseMatrix H = db.getHResistance(L);
        DenseMatrix Dr = db.getD(H);

        Double avgDs = Ds.sum().sum().s() / (Ds.cols * (Ds.cols - 1));
        Double avgDr = Dr.sum().sum().s() / (Dr.cols * (Dr.cols - 1));
        Double norm = avgDs / avgDr;

        return Ds.mul(1 - lambda).add(Dr.mul(lambda * norm));
    }
}

