package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.util.Map;

public class NullEstimator implements Estimator {
    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public Map<Integer, Integer> predict(DenseMatrix D) {
        return null;
    }
}
