package com.thesis;

import com.thesis.matrix.CustomUtils;
import com.thesis.metric.Distances;
import com.thesis.print.PrintUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public class Main {

    public static void main(String[] args) {
	    double[][] aSrc = {{0, 1, 0, 0},
                           {1, 0, 0, 1},
                           {1, 1, 0, 0},
                           {1, 0, 1, 0}};

        RealMatrix A = MatrixUtils.createRealMatrix(aSrc);
        RealMatrix L = CustomUtils.getL(A);

        for (Distances item : Distances.values()) {
            RealMatrix D = item.getD(L, A, 0.5);
            System.out.println(item.getName());
            PrintUtils.printArray(D);
            System.out.println();
        }
    }
}
