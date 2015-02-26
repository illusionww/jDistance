package com.thesis;

import com.thesis.utils.PrintUtils;
import com.thesis.metric.Distance;
import com.thesis.metric.DistancesBuilder;
import org.jblas.DoubleMatrix;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static com.thesis.helper.DistancesHelper.*;

public class DistancesTest {

    @Test
    public void testChainGraphAllDistancesItemsMoreThanZero() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(chainGraph, i);
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
    public void testChainGraphAllDistancesSymmetryMatrix() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(chainGraph, i);
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
    public void testChainGraphAllDistancesMainDiagonalZero() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                double i = multiplier * idx / 20 + 0.0001;
                DoubleMatrix result = distance.getD(chainGraph, i);
                double resultArray[][] = result.toArray2();
                for (int j = 0; j < resultArray.length; j++) {
                    assertTrue(distance.getName() + ", parameter = " + i + " diagonal not zero:\n" + PrintUtils.arrayAsString(resultArray),
                            resultArray[j][j] == 0);
                }
            });
        }
    }

    @Test
    public void testChainGraphShortestPathDistance() {
        DoubleMatrix D = DistancesBuilder.getDShortestPath(chainGraph);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphResistanceDistance() {
        DoubleMatrix L = DistancesBuilder.getL(chainGraph);
        DoubleMatrix H = DistancesBuilder.getHResistance(L);
        DoubleMatrix D = DistancesBuilder.getD(H);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphWalkDistance() {
        Distance distance = Distance.WALK;
        DoubleMatrix D = distance.getD(chainGraph, 1.0);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.975 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.975));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphLogarithmicForestDistance() {
        Distance distance = Distance.LOGARITHMIC_FOREST;
        DoubleMatrix D = distance.getD(chainGraph, 2.0);
        double multiplier = 0.959 / D.get(0, 1);
        assertTrue("distances not equal: 0.959 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.959));
        assertTrue("distances not equal: 1.081 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.081));
        assertTrue("distances not equal: 2.040 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.040));
        assertTrue("distances not equal: 2.999 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 2.999));
    }

    @Test
    public void testChainGraphPlainForestDistance() {
        Distance distance = Distance.PLAIN_FOREST;
        DoubleMatrix D = distance.getD(chainGraph, 1.0);
        double multiplier = 1.026 / D.get(0, 1);
        assertTrue("distances not equal: 1.026 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.026));
        assertTrue("distances not equal: 0.947 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.947));
        assertTrue("distances not equal: 1.500 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.500));
        assertTrue("distances not equal: 1.895 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.895));
    }

    @Test
    public void testChainGraphPlainWalkDistance() {
        Distance distance = Distance.PLAIN_WALK;
        DoubleMatrix D = distance.getD(chainGraph, 4.5);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.541 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.541));
        assertTrue("distances not equal: 1.466 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.466));

        D = distance.getD(chainGraph, 1.0);
        multiplier = 0.988 / D.get(0, 1);
        assertTrue("distances not equal: 0.988 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.988));
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.025));
        assertTrue("distances not equal: 1.379 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.379));
        assertTrue("distances not equal: 1.416 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.416));
    }

    @Test
    public void testChainGraphCommunicabilityDistance() {
        Distance distance = Distance.COMMUNICABILITY;
        DoubleMatrix D = distance.getD(chainGraph, 1.0);
        double multiplier = 0.964 / D.get(0, 1);
        assertTrue("distances not equal: 0.964 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.964));
        assertTrue("distances not equal: 1.072 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.072));
        assertTrue("distances not equal: 1.492 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.492));
        assertTrue("distances not equal: 1.564 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.564));
    }

    @Test
    public void testTriangleGraphSP_CTDistance() {
        Distance distance = Distance.COMBINATIONS;
        DoubleMatrix D = distance.getD(triangleGraph, 0);
        assertTrue("SP distance attitude not equal 1.0: " + D.get(0, 1) + "/" + D.get(1, 2), equalDouble(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(triangleGraph, 1);
        assertTrue("CT distance attitude not equal 1.5: " + D.get(0, 1) + "/" + D.get(1, 2), equalDouble(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphLogarithmicForestDistance() {
        Distance distance = Distance.LOGARITHMIC_FOREST;
        DoubleMatrix D = distance.getD(triangleGraph, 0.01);
        assertTrue("Logarithmic Forest distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(triangleGraph, 500.0);
        assertTrue("Logarithmic Forest distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphFEDistance() {
        Distance distance = Distance.HELMHOLTZ_FREE_ENERGY;
        DoubleMatrix D = distance.getD(triangleGraph, 0.0001);
        assertTrue("Free Energy distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
        D = distance.getD(triangleGraph, 30.0);
        assertTrue("Free Energy distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
    }
}
