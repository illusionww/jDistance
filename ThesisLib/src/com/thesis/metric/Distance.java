package com.thesis.metric;

import jeigen.DenseMatrix;

public abstract class Distance {
    public abstract String getName();

    public abstract void setName(String shortName);

    public abstract Scale getScale();

    public abstract void setScale(Scale scale);

    public abstract DenseMatrix getD(DenseMatrix A, double t);

    public NodesDistanceDTO getBiggestDistance(DenseMatrix A, double t) {
        NodesDistanceDTO p = new NodesDistanceDTO(0, 0, 0);
        double[][] D = DistancesHelper.toArray2(getD(A, t));
        for (int i = 0; i < D.length; i++) {
            for(int j = 0; j < D[i].length; j++) {
                if (p.getValue() < D[i][j]) {
                    p.setValue(i, j, D[i][j]);
                }
            }
        }
        return p;
    }

    public String toString() {
        return getName();
    }
}
