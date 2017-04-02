package com.jdistance;

import com.jdistance.learning.Estimator;
import org.junit.Test;

import java.util.Map;

public class WardTests {
    @Test
    public void testWardDiplomaTest() {
        Map<Integer, Integer> y_pred =  Estimator.WARD.predict(TestHelperLib.diplomaMatrix, 2);
        System.out.println(y_pred);
    }
}
