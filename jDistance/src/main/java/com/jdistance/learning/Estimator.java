package com.jdistance.learning;

import org.jblas.DoubleMatrix;

import java.io.Serializable;
import java.util.Map;

public interface Estimator extends Serializable {
    String getName();

    Map<Integer, Integer> predict(DoubleMatrix D);
}
