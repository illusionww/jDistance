package com.thesis.metric;

import jeigen.DenseMatrix;

public class SP_CT extends Distance {
    String name = "Convex combination of the shortest path and resistance metrics";
    String shortName = "SP-CT";
    Scale scale = Scale.LINEAR;

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
    public DenseMatrix getD(DenseMatrix A, double lambda) {
        if (lambda < 0.0 || lambda > 1.0) {
            throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
        }

        DistancesBuilder db = new DistancesBuilder();
        DenseMatrix L = db.getL(A);
        DenseMatrix Ds = db.getDShortestPath(A);
        DenseMatrix H = db.getHResistance(L);
        DenseMatrix Dr = db.getD(H);
        return Ds.mul(1 - lambda).add(Dr.mul(lambda));
    }
}

