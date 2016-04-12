package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.util.HashMap;

public interface Estimator {
    String getName();

    HashMap<Integer, Integer> predict(DenseMatrix D);
}
