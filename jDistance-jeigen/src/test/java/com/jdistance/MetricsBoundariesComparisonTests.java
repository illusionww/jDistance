package com.jdistance;

import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.helpers.Shortcuts;
import jeigen.DenseMatrix;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MetricsBoundariesComparisonTests {
    @Test
    public void testChainGraphSP_CTEquality() {
        Distance distance = Distance.SP_CT;
        double[][] SP = TestHelperLib.toArray2(distance.getD(TestHelperLib.chainGraph, 0));
        double[][] CT = TestHelperLib.toArray2(distance.getD(TestHelperLib.chainGraph, 1));
        for (int i = 0; i < TestHelperLib.chainGraph.cols; i++) {
            for (int j = 0; j < TestHelperLib.chainGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        TestHelperLib.equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testBigChainGraphSP_CTEquality() {
        double[][] bigChain = new double[100][100];
        for (int i = 0; i < 100; i++) {
            if (i + 1 < 100) {
                bigChain[i][i + 1] = 1.0;
            }
            if (i - 1 >= 0) {
                bigChain[i][i - 1] = 1.0;
            }
        }
        DenseMatrix chainGraph = new DenseMatrix(bigChain);
        Distance distance = Distance.SP_CT;
        double[][] SP = TestHelperLib.toArray2(distance.getD(chainGraph, 0));
        double[][] CT = TestHelperLib.toArray2(distance.getD(chainGraph, 1));
        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        TestHelperLib.equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testFullGraphSP_CTEquality() {
        Distance distance = Distance.SP_CT;
        double[][] SP = TestHelperLib.toArray2(distance.getD(TestHelperLib.fullGraph, 0));
        double[][] CT = TestHelperLib.toArray2(distance.getD(TestHelperLib.fullGraph, 1));

        for (int i = 0; i < TestHelperLib.fullGraph.cols; i++) {
            for (int j = 0; j < TestHelperLib.fullGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        TestHelperLib.equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testFullGraphSPLogForWalkEquality() {
        DenseMatrix graph = TestHelperLib.fullGraph;
        Double parameter = 0.000001;

        double[][] SP = TestHelperLib.toArray2(Shortcuts.normalize(Distance.SP_CT.getD(graph, parameter)));
        double[][] logFor = TestHelperLib.toArray2(Shortcuts.normalize(Distance.LOG_FOR.getD(graph, parameter)));
        double[][] Walk = TestHelperLib.toArray2(Shortcuts.normalize(Distance.WALK.getD(graph, parameter)));

        for (int i = 0; i < TestHelperLib.chainGraph.cols; i++) {
            for (int j = 0; j < TestHelperLib.chainGraph.cols; j++) {
                assertTrue("SP, logFor and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", logFor=" + logFor[i][j] + ", Walk=" + Walk[i][j],
                        TestHelperLib.equalDoubleStrict(SP[i][j], logFor[i][j]) && TestHelperLib.equalDoubleStrict(SP[i][j], Walk[i][j]));
            }
        }
    }
}
