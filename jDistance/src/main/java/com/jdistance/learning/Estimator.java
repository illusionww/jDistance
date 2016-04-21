package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.Map;

public interface Estimator {
    String getName();

    Map<Integer, Integer> predict(DenseMatrix D);
}
