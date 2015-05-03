package com.thesis.metric.impl;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.metric.builder.BDBuilder;
import com.thesis.utils.BDMatrix;
import com.thesis.utils.MatrixAdapter;
import jeigen.DenseMatrix;

public class LogComm_BD extends Distance {
    String name = "logComm";
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
        BDMatrix H0 = db.getH0Communicability(A_BD, t);
        BDMatrix H = db.H0toH(H0);
        BDMatrix D = db.getD(H);
        BDMatrix result = db.sqrtD(D);
        BDMatrix norm = db.normalization(result);
        return MatrixAdapter.toDense(norm);
    }
}
