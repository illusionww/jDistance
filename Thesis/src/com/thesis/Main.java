package com.thesis;

import com.thesis.matrix.MatrixUtils;
import com.thesis.metric.Distances;
import com.thesis.print.PrintUtils;
import org.jblas.FloatMatrix;

public class Main {

    public static void main(String[] args) {
	    float[][] aSrc = {{0, 1, 0, 0},
                           {1, 0, 0, 1},
                           {1, 1, 0, 0},
                           {1, 0, 1, 0}};

        FloatMatrix A = new FloatMatrix(aSrc);
        PrintUtils.printArray(A, "A");

        FloatMatrix L = MatrixUtils.getL(A);
        PrintUtils.printArray(L, "L");

        for (Distances item : Distances.values()) {
            FloatMatrix D = item.getD(L, A, (float)0.5);
            PrintUtils.printArray(D, item.getName());
        }
    }
}
