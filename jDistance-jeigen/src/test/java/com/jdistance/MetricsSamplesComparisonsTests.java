package com.jdistance;

import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.helpers.Shortcuts;
import com.jdistance.learning.measure.Scale;
import jeigen.DenseMatrix;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class MetricsSamplesComparisonsTests {
    @Test
    public void testChainGraphShortestPathDistance() {
        DenseMatrix D = Shortcuts.getD_SP(TestHelperLib.chainGraph);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphResistanceDistance() {
        DenseMatrix L = Shortcuts.getL(TestHelperLib.chainGraph);
        DenseMatrix H = Shortcuts.getH_R(TestHelperLib.chainGraph);
        DenseMatrix D = Shortcuts.HtoD(H);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphWalkDistance() {
        Distance distance = Distance.WALK;
        Double parameter = Scale.alphaToT.calc(TestHelperLib.chainGraph, 1.0);
        DenseMatrix D = distance.getD(TestHelperLib.chainGraph, parameter);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.975 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 1.975));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphLogarithmicForestDistance() {
        Distance distance = Distance.LOG_FOR;
        DenseMatrix D = distance.getD(TestHelperLib.chainGraph, 2.0);
        double multiplier = 0.959 / D.get(0, 1);
        assertTrue("distances not equal: 0.959 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 0.959));
        assertTrue("distances not equal: 1.081 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 1.081));
        assertTrue("distances not equal: 2.040 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 2.040));
        assertTrue("distances not equal: 2.999 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 2.999));
    }

    @Test
    public void testChainGraphPlainForestDistance() {
        Distance distance = Distance.FOR;
        DenseMatrix D = distance.getD(TestHelperLib.chainGraph, 1.0);
        double multiplier = 1.026 / D.get(0, 1);
        assertTrue("distances not equal: 1.026 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 1.026));
        assertTrue("distances not equal: 0.947 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 0.947));
        assertTrue("distances not equal: 1.500 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 1.500));
        assertTrue("distances not equal: 1.895 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 1.895));
    }

    @Test
    public void testChainGraphPlainWalkDistance() {
        Distance distance = Distance.P_WALK;
        Double parameter = Scale.alphaToT.calc(TestHelperLib.chainGraph, 4.5);
        DenseMatrix D = distance.getD(TestHelperLib.chainGraph, parameter);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.541 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 1.541));
        assertTrue("distances not equal: 1.466 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 1.466));

        parameter = Scale.alphaToT.calc(TestHelperLib.chainGraph, 1.0);
        D = distance.getD(TestHelperLib.chainGraph, parameter);
        multiplier = 0.988 / D.get(0, 1);
        assertTrue("distances not equal: 0.988 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 0.988));
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 1.025));
        assertTrue("distances not equal: 1.379 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 1.379));
        assertTrue("distances not equal: 1.416 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 1.416));
    }

    @Test
    public void testChainGraphCommunicabilityDistance() {
        Distance distance = Distance.COMM;
        DenseMatrix D = distance.getD(TestHelperLib.chainGraph, 1.0);
        double multiplier = 0.964 / D.get(0, 1);
        assertTrue("distances not equal: 0.964 != " + multiplier * D.get(0, 1), TestHelperLib.equalDouble(multiplier * D.get(0, 1), 0.964));
        assertTrue("distances not equal: 1.072 != " + multiplier * D.get(1, 2), TestHelperLib.equalDouble(multiplier * D.get(1, 2), 1.072));
        assertTrue("distances not equal: 1.492 != " + multiplier * D.get(0, 2), TestHelperLib.equalDouble(multiplier * D.get(0, 2), 1.492));
        assertTrue("distances not equal: 1.564 != " + multiplier * D.get(0, 3), TestHelperLib.equalDouble(multiplier * D.get(0, 3), 1.564));
    }

    @Test
    public void testTriangleGraphSP_CTDistance() {
        Distance distance = Distance.SP_CT;
        DenseMatrix D = distance.getD(TestHelperLib.triangleGraph, 0);
        assertTrue("SP distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDouble(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(TestHelperLib.triangleGraph, 1);
        assertTrue("CT distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDouble(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphLogarithmicForestDistance() {
        Distance distance = Distance.LOG_FOR;
        DenseMatrix D = distance.getD(TestHelperLib.triangleGraph, 0.01);
        assertTrue("Logarithmic Forest distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(TestHelperLib.triangleGraph, 500.0);
        assertTrue("Logarithmic Forest distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphFEDistance() {
        Distance distance = Distance.FE;
        DenseMatrix D = distance.getD(TestHelperLib.triangleGraph, 0.0001);
        assertTrue("Free Energy distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
        D = distance.getD(TestHelperLib.triangleGraph, 30.0);
        assertTrue("Free Energy distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), TestHelperLib.equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
    }

    @Test
    public void testTreeGraphSP_CTEquality() {
        Distance distance = Distance.SP_CT;
        double[][] SP = TestHelperLib.toArray2(distance.getD(TestHelperLib.treeMatrix, 0));
        double[][] CT = TestHelperLib.toArray2(distance.getD(TestHelperLib.treeMatrix, 1));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        TestHelperLib.equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testChainGraphForestDistance() {
        DenseMatrix H0 = Kernel.FOR_H.getK(TestHelperLib.chainGraph, 0);
        double[][] forChain0 = TestHelperLib.toArray2(H0);
        double[][] forChain0etalon = {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        assertTrue(Arrays.deepToString(forChain0), TestHelperLib.equalArraysStrict(forChain0, forChain0etalon));

        DenseMatrix H05 = Kernel.FOR_H.getK(TestHelperLib.chainGraph, 0.5);
        double[][] forChain05 = TestHelperLib.toArray2(H05);
        double[][] forChain05etalon = {
                {0.73214286, 0.19642857, 0.05357143, 0.01785714},
                {0.19642857, 0.58928571, 0.16071429, 0.05357143},
                {0.05357143, 0.16071429, 0.58928571, 0.19642857},
                {0.01785714, 0.05357143, 0.19642857, 0.73214286}
        };
        assertTrue(Arrays.deepToString(forChain05), TestHelperLib.equalArraysStrict(forChain05, forChain05etalon));
    }

    @Test
    public void testTriangleGraphForestDistance() {
        DenseMatrix H0 = Kernel.FOR_H.getK(TestHelperLib.triangleGraph, 0.2);
        double[][] forChain02 = TestHelperLib.toArray2(H0);
        double[][] forChain0etalon = {{0.85185185, 0.11111111, 0.01851852, 0.01851852},
                {0.11111111, 0.66666667, 0.11111111, 0.11111111},
                {0.01851852, 0.11111111, 0.74768519, 0.12268519},
                {0.01851852, 0.11111111, 0.12268519, 0.74768519}};
        assertTrue(Arrays.deepToString(forChain02), TestHelperLib.equalArraysStrict(forChain02, forChain0etalon));
    }
}
