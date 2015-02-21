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
            {1, 0, 1, 0},
            {0, 1, 1, 1},
            {1, 0, 1, 1},
            {1, 1, 0, 1}
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
}
