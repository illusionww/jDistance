package com.jdistance.learning;

import jeigen.DenseMatrix;

import java.io.Serializable;
import java.util.Map;

public interface Estimator extends Serializable {
    String getName();

    Map<Integer, Integer> predict(DenseMatrix D);
}
