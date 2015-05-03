package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.metric.builder.BDBuilder;
import com.thesis.utils.BDMatrix;
import com.thesis.utils.MatrixAdapter;
import jeigen.DenseMatrix;

public class Comm_BD extends Distance {
    String name = "Comm_BD";
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
        BDMatrix A_BD = MatrixAdapter.toBD(A);
        BDBuilder db = new BDBuilder();
        BDMatrix H = db.getH0Communicability(A_BD, t);
        BDMatrix D = db.getD(H);
        BDMatrix result = db.sqrtD(D);
        BDMatrix norm = db.normalization(result);
        return MatrixAdapter.toDense(norm);
    }
}
