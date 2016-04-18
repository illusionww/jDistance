package com.jdistance;

import com.jdistance.metric.Metric;
import com.jdistance.metric.Shortcuts;
import jeigen.DenseMatrix;
import org.junit.Test;

import static com.jdistance.TestHelperLib.*;
import static com.jdistance.TestHelperLib.chainGraph;
import static com.jdistance.TestHelperLib.equalDoubleStrict;
import static org.junit.Assert.assertTrue;

public class MetricsBoundariesComparisonTests {
    @Test
    public void testChainGraphSP_CTEquality() {
        Metric distance = Metric.SP_CT;
        double[][] SP = toArray2(distance.getD(chainGraph, 0));
        double[][] CT = toArray2(distance.getD(chainGraph, 1));
        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        equalDoubleStrict(SP[i][j], CT[i][j]));
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
        Metric distance = Metric.SP_CT;
        double[][] SP = toArray2(distance.getD(chainGraph, 0));
        double[][] CT = toArray2(distance.getD(chainGraph, 1));
        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testFullGraphSP_CTEquality() {
        Metric distance = Metric.SP_CT;
        double[][] SP = toArray2(distance.getD(fullGraph, 0));
        double[][] CT = toArray2(distance.getD(fullGraph, 1));

        for (int i = 0; i < fullGraph.cols; i++) {
            for (int j = 0; j < fullGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testFullGraphSPLogForWalkEquality() {
        DenseMatrix graph = fullGraph;
        Double parameter = 0.000001;

        double[][] SP = toArray2(Shortcuts.normalize(Metric.SP_CT.getD(graph, parameter)));
        double[][] logFor = toArray2(Shortcuts.normalize(Metric.LOG_FOR.getD(graph, parameter)));
        double[][] Walk = toArray2(Shortcuts.normalize(Metric.WALK.getD(graph, parameter)));

        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP, logFor and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", logFor=" + logFor[i][j] + ", Walk=" + Walk[i][j],
                        equalDoubleStrict(SP[i][j], logFor[i][j]) && equalDoubleStrict(SP[i][j], Walk[i][j]));
            }
        }
    }
}
