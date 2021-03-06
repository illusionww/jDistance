package com.jdistance;

import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.DistanceWrapper;
import com.jdistance.learning.measure.Scale;
import jeigen.DenseMatrix;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class MetricsCommonTests {

    @Test
    public void testChainGraphAllDistancesItemsMoreThanZero() {
        double from = 0.0001;
        double to = 0.95;
        int pointsCount = 30;

        List<DistanceWrapper> values = Distance.getAll();
        values.stream().filter(value -> Distance.RSP.getName().equals(value.getName()) || Distance.FE.getName().equals(value.getName())).forEach(value -> value.setScale(Scale.FRACTION));

        values.forEach(metric -> {
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = metric.getScale().calc(TestHelperLib.chainGraph, base);
                DenseMatrix result = metric.calc(TestHelperLib.chainGraph, i);
                for (double item : result.getValues()) {
                    assertTrue(metric.getName() + ", parameter = " + i + ":  matrix element less than zero or NaN:\n" + result, item >= 0);
                }
            });
        });
    }

    @Test
    public void testChainGraphAllDistancesSymmetryMatrix() {
        double from = 0.005;
        double to = 0.5;
        int pointsCount = 30;

        Distance values[] = Distance.values();
        Arrays.asList(values).forEach(metric -> {
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = metric.getScale().calc(TestHelperLib.chainGraph, base);
                DenseMatrix result = metric.getD(TestHelperLib.chainGraph, i);
                double resultArray[][] = TestHelperLib.toArray2(result);
                int d = resultArray.length;
                for (int j = 1; j < d - 1; j++) {
                    for (int k = j + 1; k < d; k++) {
                        assertTrue(metric.getName() + ", parameter = " + i + " (" + base + "):  matrix isn't symmetry:\n" + Arrays.deepToString(resultArray),
                                TestHelperLib.equalDoubleStrict(resultArray[j][k], resultArray[k][j]));
                    }
                }
            });
        });
    }

    @Test
    public void testChainGraphAllDistancesMainDiagonalZero() {
        double from = 0.0001;
        double to = 0.5;
        int pointsCount = 30;

        Distance values[] = Distance.values();
        Arrays.asList(values).forEach(metric -> {
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = metric.getScale().calc(TestHelperLib.chainGraph, base);
                DenseMatrix result = metric.getD(TestHelperLib.chainGraph, i);
                double resultArray[][] = TestHelperLib.toArray2(result);
                for (int j = 0; j < resultArray.length; j++) {
                    assertTrue(metric.getName() + ", parameter = " + i + " diagonal not zero:\n" + result, resultArray[j][j] == 0);
                }
            });
        });
    }

}
