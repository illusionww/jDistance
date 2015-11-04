package com.jdistance;

import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import com.jdistance.metric.JeigenBuilder;
import com.jdistance.metric.Scale;
import com.jdistance.utils.MatrixAdapter;
import jeigen.DenseMatrix;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jdistance.helper.TestHelperLib.*;
import static org.junit.Assert.assertTrue;

public class DistancesTest {

    @Test
    public void testChainGraphAllDistancesItemsMoreThanZero() {
        double from = 0.0001;
        double to = 0.5;
        int pointsCount = 30;

        DistanceClass values[] = DistanceClass.values();
        Arrays.asList(values).forEach(value -> {
            Distance distance = value.getInstance();
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = distance.getScale().calc(chainGraph, base);
                DenseMatrix result = distance.getD(chainGraph, i);
                for (double item : result.getValues()) {
                    assertTrue(distance.getName() + ", parameter = " + i + ":  matrix element less than zero or NaN:\n" + result, item >= 0);
                }
            });
        });
    }

    @Test
    public void testChainGraphAllDistancesSymmetryMatrix() {
        double from = 0.0001;
        double to = 0.5;
        int pointsCount = 30;

        DistanceClass values[] = DistanceClass.values();
        Arrays.asList(values).forEach(value -> {
            Distance distance = value.getInstance();
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = distance.getScale().calc(chainGraph, base);
                DenseMatrix result = distance.getD(chainGraph, i);
                double resultArray[][] = MatrixAdapter.toArray2(result);
                int d = resultArray.length;
                for (int j = 1; j < d - 1; j++) {
                    for (int k = j + 1; k < d; k++) {
                        assertTrue(distance.getName() + ", parameter = " + i + ":  matrix isn't symmetry:\n" + resultArray,
                                equalDoubleStrict(resultArray[j][k], resultArray[k][j]));
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

        DistanceClass values[] = DistanceClass.values();
        Arrays.asList(values).forEach(value -> {
            Distance distance = value.getInstance();
            double step = (to - from) / (pointsCount - 1);
            IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
                Double base = from + idx * step;
                Double i = distance.getScale().calc(chainGraph, base);
                DenseMatrix result = distance.getD(chainGraph, i);
                double resultArray[][] = MatrixAdapter.toArray2(result);
                for (int j = 0; j < resultArray.length; j++) {
                    assertTrue(distance.getName() + ", parameter = " + i + " diagonal not zero:\n" + result, resultArray[j][j] == 0);
                }
            });
        });
    }

    @Test
    public void testChainGraphShortestPathDistance() {
        JeigenBuilder db = new JeigenBuilder();
        DenseMatrix D = db.getDShortestPath(chainGraph);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphResistanceDistance() {
        JeigenBuilder db = new JeigenBuilder();
        DenseMatrix L = db.getL(chainGraph);
        DenseMatrix H = db.getHResistance(L);
        DenseMatrix D = db.getD(H);
        double multiplier = 1.0 / D.get(0, 1);
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.000));
        assertTrue("distances not equal: 1.000 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.000));
        assertTrue("distances not equal: 2.000 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.000));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphResistanceDistanceNormalization() {
        JeigenBuilder db = new JeigenBuilder();
        DenseMatrix L = db.getL(triangleGraph);
        DenseMatrix H = db.getHResistance(L);
        DenseMatrix D = db.getD(H);
        assertTrue("distances not equal: 1.000 != " + D.get(0, 1), equalDouble(D.get(0, 1), 1.000));
    }

    @Test
    public void testChainGraphWalkDistance() {
        Distance distance = DistanceClass.WALK.getInstance();
        Double parameter = Scale.alphaToT.calc(chainGraph, 1.0);
        DenseMatrix D = distance.getD(chainGraph, parameter);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.975 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.975));
        assertTrue("distances not equal: 3.000 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 3.000));
    }

    @Test
    public void testChainGraphLogarithmicForestDistance() {
        Distance distance = DistanceClass.LOG_FOREST.getInstance();
        DenseMatrix D = distance.getD(chainGraph, 2.0);
        double multiplier = 0.959 / D.get(0, 1);
        assertTrue("distances not equal: 0.959 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.959));
        assertTrue("distances not equal: 1.081 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.081));
        assertTrue("distances not equal: 2.040 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 2.040));
        assertTrue("distances not equal: 2.999 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 2.999));
    }

    @Test
    public void testChainGraphPlainForestDistance() {
        Distance distance = DistanceClass.FOREST.getInstance();
        DenseMatrix D = distance.getD(chainGraph, 1.0);
        double multiplier = 1.026 / D.get(0, 1);
        assertTrue("distances not equal: 1.026 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.026));
        assertTrue("distances not equal: 0.947 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.947));
        assertTrue("distances not equal: 1.500 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.500));
        assertTrue("distances not equal: 1.895 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.895));
    }

    @Test
    public void testChainGraphPlainWalkDistance() {
        Distance distance = DistanceClass.PLAIN_WALK.getInstance();
        Double parameter = Scale.alphaToT.calc(chainGraph, 4.5);
        DenseMatrix D = distance.getD(chainGraph, parameter);
        double multiplier = 1.025 / D.get(0, 1);
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 1.025));
        assertTrue("distances not equal: 0.950 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 0.950));
        assertTrue("distances not equal: 1.541 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.541));
        assertTrue("distances not equal: 1.466 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.466));

        parameter = Scale.alphaToT.calc(chainGraph, 1.0);
        D = distance.getD(chainGraph, parameter);
        multiplier = 0.988 / D.get(0, 1);
        assertTrue("distances not equal: 0.988 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.988));
        assertTrue("distances not equal: 1.025 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.025));
        assertTrue("distances not equal: 1.379 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.379));
        assertTrue("distances not equal: 1.416 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.416));
    }

    @Test
    public void testChainGraphCommunicabilityDistance() {
        Distance distance = DistanceClass.COMM.getInstance();
        DenseMatrix D = distance.getD(chainGraph, 1.0);
        double multiplier = 0.964 / D.get(0, 1);
        assertTrue("distances not equal: 0.964 != " + multiplier * D.get(0, 1), equalDouble(multiplier * D.get(0, 1), 0.964));
        assertTrue("distances not equal: 1.072 != " + multiplier * D.get(1, 2), equalDouble(multiplier * D.get(1, 2), 1.072));
        assertTrue("distances not equal: 1.492 != " + multiplier * D.get(0, 2), equalDouble(multiplier * D.get(0, 2), 1.492));
        assertTrue("distances not equal: 1.564 != " + multiplier * D.get(0, 3), equalDouble(multiplier * D.get(0, 3), 1.564));
    }

    @Test
    public void testTriangleGraphSP_CTDistance() {
        Distance distance = DistanceClass.SP_CT.getInstance();
        DenseMatrix D = distance.getD(triangleGraph, 0);
        assertTrue("SP distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), equalDouble(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(triangleGraph, 1);
        assertTrue("CT distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), equalDouble(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphLogarithmicForestDistance() {
        Distance distance = DistanceClass.LOG_FOREST.getInstance();
        DenseMatrix D = distance.getD(triangleGraph, 0.01);
        assertTrue("Logarithmic Forest distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
        D = distance.getD(triangleGraph, 500.0);
        assertTrue("Logarithmic Forest distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
    }

    @Test
    public void testTriangleGraphFEDistance() {
        Distance distance = DistanceClass.FREE_ENERGY.getInstance();
        DenseMatrix D = distance.getD(triangleGraph, 0.0001);
        assertTrue("Free Energy distance attitude not equal 1.5: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.5));
        D = distance.getD(triangleGraph, 30.0);
        assertTrue("Free Energy distance attitude not equal 1.0: " + D.get(0, 1) / D.get(1, 2), equalDoubleNonStrict(D.get(0, 1) / D.get(1, 2), 1.0));
    }

    @Test
    public void testTreeGraphSP_CTEquality() {
        Distance distance = DistanceClass.SP_CT.getInstance();
        double[][] SP = MatrixAdapter.toArray2(distance.getD(treeMatrix, 0));
        double[][] CT = MatrixAdapter.toArray2(distance.getD(treeMatrix, 1));
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testChainGraphForestDistance() {
        JeigenBuilder builder = new JeigenBuilder();

        DenseMatrix L = builder.getL(chainGraph);
        DenseMatrix H0 = builder.getH0Forest(L, 0);
        double[][] forChain0 = MatrixAdapter.toArray2(H0);
        double[][] forChain0etalon = {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};
        assertTrue(Arrays.deepToString(forChain0), equalArraysStrict(forChain0, forChain0etalon));

        DenseMatrix H05 = builder.getH0Forest(L, 0.5);
        double[][] forChain05 = MatrixAdapter.toArray2(H05);
        double[][] forChain05etalon = {{0.73214286, 0.19642857, 0.05357143, 0.01785714},
                {0.19642857, 0.58928571, 0.16071429, 0.05357143},
                {0.05357143, 0.16071429, 0.58928571, 0.19642857},
                {0.01785714, 0.05357143, 0.19642857, 0.73214286}};
        assertTrue(Arrays.deepToString(forChain05), equalArraysStrict(forChain05, forChain05etalon));
    }

    @Test
    public void testTriangleGraphForestDistance() {
        JeigenBuilder builder = new JeigenBuilder();

        DenseMatrix L = builder.getL(triangleGraph);
        DenseMatrix H0 = builder.getH0Forest(L, 0.2);
        double[][] forChain02 = MatrixAdapter.toArray2(H0);
        double[][] forChain0etalon = {{0.85185185, 0.11111111, 0.01851852, 0.01851852},
                {0.11111111, 0.66666667, 0.11111111, 0.11111111},
                {0.01851852, 0.11111111, 0.74768519, 0.12268519},
                {0.01851852, 0.11111111, 0.12268519, 0.74768519}};
        assertTrue(Arrays.deepToString(forChain02), equalArraysStrict(forChain02, forChain0etalon));
    }

    @Test
    public void testChainGraphSP_CTEquality() {
        Distance distance = DistanceClass.SP_CT.getInstance();
        double[][] SP = MatrixAdapter.toArray2(distance.getD(chainGraph, 0));
        double[][] CT = MatrixAdapter.toArray2(distance.getD(chainGraph, 1));
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
        Distance distance = DistanceClass.SP_CT.getInstance();
        double[][] SP = MatrixAdapter.toArray2(distance.getD(chainGraph, 0));
        double[][] CT = MatrixAdapter.toArray2(distance.getD(chainGraph, 1));
        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", CT=" + CT[i][j],
                        equalDoubleStrict(SP[i][j], CT[i][j]));
            }
        }
    }

    @Test
    public void testFullGraphSP_CTEquality() {
        Distance distance = DistanceClass.SP_CT.getInstance();
        double[][] SP = MatrixAdapter.toArray2(distance.getD(fullGraph, 0));
        double[][] CT = MatrixAdapter.toArray2(distance.getD(fullGraph, 1));

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

        double[][] SP = MatrixAdapter.toArray2(JeigenBuilder.normalization(DistanceClass.SP_CT.getInstance().getD(graph, parameter)));
        double[][] logFor = MatrixAdapter.toArray2(JeigenBuilder.normalization(DistanceClass.LOG_FOREST.getInstance().getD(graph, parameter)));
        double[][] Walk = MatrixAdapter.toArray2(JeigenBuilder.normalization(DistanceClass.WALK.getInstance().getD(graph, parameter)));

        for (int i = 0; i < chainGraph.cols; i++) {
            for (int j = 0; j < chainGraph.cols; j++) {
                assertTrue("SP, logFor and CT distance not equal: (" + i + ", " + j + ") SP=" + SP[i][j] + ", logFor=" + logFor[i][j] + ", Walk=" + Walk[i][j],
                        equalDoubleStrict(SP[i][j], logFor[i][j]) && equalDoubleStrict(SP[i][j], Walk[i][j]));
            }
        }
    }

    @Test
    public void testForDiploma() {
        DenseMatrix graph = diplomaMatrix;
        Double parameter = 0.2;

        for (DistanceClass clazz : DistanceClass.values()) {
            System.out.println(clazz.getInstance().getName());
            double[][] array = MatrixAdapter.toArray2(clazz.getInstance().getD(graph, parameter));
            for (double[] anArray : array) {
                for (double anAnArray : anArray) {
                    System.out.printf("%.3f ", anAnArray);
                }
                System.out.println();
            }
        }
    }
}
