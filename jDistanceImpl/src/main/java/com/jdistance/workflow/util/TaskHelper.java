package com.jdistance.workflow.util;

import java.util.Arrays;

public class TaskHelper {
    public static double[] standardize(double[] d) {
        int dim = (int) Math.sqrt(d.length);

        // найдем и вычтем среднее
        Double sum = Arrays.stream(d).sum();
        Double average = sum / (double) (d.length - dim);
        d = Arrays.stream(d).map(value -> value - average).toArray();

        // обнулим диагональ
        for (int c = 0; c < dim; c++) {
            d[c * dim + c] = 0;
        }

        // найдем среднеквадратичное отклонение и поделим на него
        Double deviation = deviation(d);
        d = Arrays.stream(d).map(value -> value / deviation).toArray();

        return d;
    }

    public static double deviation(double[] d) {
        Double deviationRaw = 0d;
        for (double aD : d) {
            deviationRaw += aD * aD;
        }
        return Math.sqrt(deviationRaw / d.length);
    }
}
