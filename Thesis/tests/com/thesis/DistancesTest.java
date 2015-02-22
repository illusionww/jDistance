package com.thesis;

import com.thesis.helper.PrintUtils;
import com.thesis.metric.Distance;
import org.jblas.FloatMatrix;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

public class DistancesTest {
    FloatMatrix exampleMatrix = new FloatMatrix(new float[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    @Test
    public void testAllDistancesMoreThanZeroOnSimpleData() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                float i = multiplier * idx / 20f + 0.0001f;
                FloatMatrix result = distance.getD(exampleMatrix, i);
                float resultArray[][] = result.toArray2();
                for (float[] resultRow : resultArray) {
                    for (float resultItem : resultRow) {
                        assertTrue(distance.getName() + ", parameter = " + i + ":  matrix element less than zero or NaN:\n" + PrintUtils.arrayAsString(resultArray), resultItem >= 0);
                    }
                }
            });
        }
    }

    @Test
    public void testAllDistancesMainDiagonalZeroOnSimpleData() {
        Distance values[] = Distance.values();
        for (Distance distance : values) {
            int multiplier = distance.equals(Distance.COMBINATIONS) ? 1 : 10;
            IntStream.range(1, 20).boxed().collect(Collectors.toList()).forEach(idx -> {
                float i = multiplier * idx / 20f + 0.0001f;
                FloatMatrix result = distance.getD(exampleMatrix, i);
                float resultArray[][] = result.toArray2();
                for (int j = 0; j < resultArray.length; j++) {
                    assertTrue(distance.getName() + ", parameter = " + i + " diagonal not zero:\n" + PrintUtils.arrayAsString(resultArray), resultArray[j][j] == 0);
                }
            });
        }
    }

    @Test
    public void testWalkDistance() {
        Distance distance = Distance.WALK;
        FloatMatrix D = distance.getD(exampleMatrix, (float) 1.0);
        assertTrue("distances not equal: 1.025 != " + D.get(0, 1), D.get(0, 1) == 1.025);
        assertTrue("distances not equal: 0.95 != " + D.get(1, 2), D.get(1, 2) == 0.95);
    }

    @Test
    public void testLogarithmicForestDistance() {
        Distance distance = Distance.LOGARITHMIC_FOREST;
        FloatMatrix D = distance.getD(exampleMatrix, (float) 2.0);
        assertTrue("distances not equal: 0.959 != " + D.get(0, 1), D.get(0, 1) == 0.959);
        assertTrue("distances not equal: 1.081 != " + D.get(1, 2), D.get(1, 2) == 1.081);
    }

    @Test
    public void testPlainWalkDistance() {
        Distance distance = Distance.PLAIN_WALK;
        FloatMatrix D = distance.getD(exampleMatrix, (float) 4.5);
        assertTrue("distances not equal: 1.025 != " + D.get(0, 1), D.get(0, 1) == 1.025);
        assertTrue("distances not equal: 0.95 != " + D.get(1, 2), D.get(1, 2) == 0.95);
        assertTrue("distances not equal: 1.541 != " + D.get(0, 2), D.get(0, 2) == 1.541);
        assertTrue("distances not equal: 1.466 != " + D.get(0, 3), D.get(0, 3) == 1.466);

        D = distance.getD(exampleMatrix, (float) 1.0);
        assertTrue("distances not equal: 0.988 != " + D.get(0, 1), D.get(0, 1) == 0.988);
        assertTrue("distances not equal: 1.025 != " + D.get(1, 2), D.get(1, 2) == 1.025);
        assertTrue("distances not equal: 1.379 != " + D.get(0, 2), D.get(0, 2) == 1.379);
        assertTrue("distances not equal: 1.416 != " + D.get(0, 3), D.get(0, 3) == 1.416);
    }

    @Test
    public void testCommunicabilityDistance() {
        Distance distance = Distance.COMMUNICABILITY;
        FloatMatrix D = distance.getD(exampleMatrix, (float) 1.0);
        assertTrue("distances not equal: 0.964 != " + D.get(0, 1), D.get(0, 1) == 0.964);
        assertTrue("distances not equal: 1.072 != " + D.get(1, 2), D.get(1, 2) == 1.072);
        assertTrue("distances not equal: 1.492 != " + D.get(0, 2), D.get(0, 2) == 1.492);
        assertTrue("distances not equal: 1.546 != " + D.get(0, 3), D.get(0, 3) == 1.546);
    }
}
