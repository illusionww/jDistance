package com.jdistance.impl.workflow.util;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;

public class StandardizeHelper {
    public static DenseMatrix standardize(DenseMatrix D) {
        // найдем и вычтем среднее
        Double sum = Arrays.stream(D.getValues()).sum();
        Double average = sum / (D.rows * (D.cols - 1));
        D = D.sub(average);
        for (int i = 0; i < D.cols; i++) {
            D.set(i, i, 0); // обнулим диагональ
        }

        // найдем среднеквадратичное отклонение и поделим на него
        Double deviation = getDeviation(D);
        return D.div(deviation);
    }

    public static double getDeviation(DenseMatrix D) {
        Double deviationRaw = 0d;
        for (int c = 0; c < D.cols; c++) {
            for (int r = c + 1; r < D.rows; r++) {
                deviationRaw += D.get(c, r) * D.get(c, r);
            }
        }
        return Math.sqrt(deviationRaw / (D.rows * (D.cols - 1) / 2));
    }

    public static double getDeviation(List<Double> list) {
        Double deviationRaw = list.stream().reduce(0.0, (x, y) -> x + y * y);
        return Math.sqrt(deviationRaw / list.size());
    }
}
