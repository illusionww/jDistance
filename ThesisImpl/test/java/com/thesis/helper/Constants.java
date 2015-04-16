package com.thesis.helper;

import jeigen.DenseMatrix;

public class Constants {
    public static final String GRAPHML_EXAMPLE1 = "myRandomGraphn100k5pin0_3pout0_02_graphml.graphml";
    public static final String n100pin03pout01k5FOLDER = "n100pin03pout01k5";
    public static final DenseMatrix triangleGraph = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 1},
            {0, 1, 0, 1},
            {0, 1, 1, 0}
    });
}