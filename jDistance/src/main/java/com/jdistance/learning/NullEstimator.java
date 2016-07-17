package com.jdistance.learning;

import org.jblas.DoubleMatrix;

import java.util.Map;

public class NullEstimator implements Estimator {
    @Override
    public String getName() {
        return "Null";
    }

    @Override
    public Map<Integer, Integer> predict(DoubleMatrix D) {
        return null;
    }
}
