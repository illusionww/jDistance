package com.thesis;

import com.thesis.utils.PrintUtils;
import com.thesis.metric.Distance;
import com.thesis.metric.DistancesBuilder;
import org.jblas.DoubleMatrix;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class DistancesTest {
    DoubleMatrix exampleMatrix = new DoubleMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    @Test
    public void testAllDistancesItemsMoreThanZero() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(exampleMatrix, i);
                double resultArray[][] = result.toArray2();
                for (double[] resultRow : resultArray) {
                    for (double resultItem : resultRow) {
                        assertTrue(distance.getName() + ", parameter = " + i + ":  matrix element less than zero or NaN:\n" + PrintUtils.arrayAsString(resultArray),
                                resultItem >= 0);
                    }
                }
            });
        }
    }

    @Test
    public void testAllDistancesSymmetryMatrix() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(exampleMatrix, i);
                double resultArray[][] = result.toArray2();
                int d = resultArray.length;
                for (int j = 1; j < d - 1; j++) {
                    for (int k = j + 1; k < d; k++) {
                        assertTrue(distance.getName() + ", parameter = " + i + ":  matrix isn't symmetry:\n" + PrintUtils.arrayAsString(resultArray),
                                equalDoubleStrict(resultArray[j][k], resultArray[k][j]));
                    }
                }
            });
        }
    }

    @Test
    public void testAllDistancesMainDiagonalZero() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(exampleMatrix, i);
                double resultArray[][] = result.toArray2();
                for (int j = 0; j < resultArray.length; j++) {
                    assertTrue(distance.getName() + ", parameter = " + i + " diagonal not zero:\n" + PrintUtils.arrayAsString(resultArray),
                            resultArray[j][j] == 0);
                }
            });
        }
    }

    @Test
    public void testShortestPathDistance() {
        DoubleMatrix D = DistancesBuilder.getDShortestPath(exampleMatrix);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testResistanceDistance() {
        DoubleMatrix L = DistancesBuilder.getL(exampleMatrix);
        DoubleMatrix H = DistancesBuilder.getHResistance(L);
        DoubleMatrix D = DistancesBuilder.getD(H);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testWalkDistance() {
        Distance distance = Distance.WALK;
        DoubleMatrix D = distance.getD(exampleMatrix, 1.0);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.975 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.975));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testLogarithmicForestDistance() {
        Distance distance = Distance.LOGARITHMIC_FOREST;
        DoubleMatrix D = distance.getD(exampleMatrix, 2.0);
        double multiplier = 0.959 / D.get(0, 1);
        assertTrue("distances not equal: 0.959 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.959));
        assertTrue("distances not equal: 1.081 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.081));
        assertTrue("distances not equal: 2.040 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.040));
        assertTrue("distances not equal: 2.999 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 2.999));
    }

    @Test
    public void testPlainForestDistance() {
        Distance distance = Distance.PLAIN_FOREST;
        DoubleMatrix D = distance.getD(exampleMatrix, 1.0);
        double multiplier = 1.026 / D.get(0, 1);
        assertTrue("distances not equal: 1.026 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.026));
        assertTrue("distances not equal: 0.947 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.947));
        assertTrue("distances not equal: 1.500 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.500));
        assertTrue("distances not equal: 1.895 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.895));
    }

    @Test
    public void testPlainWalkDistance() {
        Distance distance = Distance.PLAIN_WALK;
        DoubleMatrix D = distance.getD(exampleMatrix, 4.5);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.541 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.541));
        assertTrue("distances not equal: 1.466 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.466));

        D = distance.getD(exampleMatrix, 1.0);
        multiplier = 0.988 / D.get(0, 1);
        assertTrue("distances not equal: 0.988 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.988));
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.025));
        assertTrue("distances not equal: 1.379 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.379));
        assertTrue("distances not equal: 1.416 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.416));
    }

    @Test
    public void testCommunicabilityDistance() {
        Distance distance = Distance.LOGARITHMIC_COMMUNICABILITY;
        DoubleMatrix D = distance.getD(exampleMatrix, 1.0);
        double multiplier = 0.964 / D.get(0, 1);
        assertTrue("distances not equal: 0.964 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.964));
        assertTrue("distances not equal: 1.072 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.072));
        assertTrue("distances not equal: 1.492 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.492));
        assertTrue("distances not equal: 1.546 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.546));
    }

    @Test
    public void testFreeEnergyDistance() {
        Distance distance = Distance.HELMHOLTZ_FREE_ENERGY;
        DoubleMatrix D = distance.getD(exampleMatrix, 1.0);
        PrintUtils.print(D, "Ф");
    }

    private boolean equalDouble(double a, double b) {
        return Math.abs(a - b) < 0.0011;
    }

    private boolean equalDoubleStrict(double a, double b) {
        return Math.abs(a - b) < 0.0000002;
    }
}
