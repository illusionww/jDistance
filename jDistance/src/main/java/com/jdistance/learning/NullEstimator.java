package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.util.HashMap;

public class NullEstimator implements Estimator {
    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public HashMap<Integer, Integer> predict(DenseMatrix D) {
        return null;
    }
}
