package com.jdistance;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.gridsearch.classifier.KNearestNeighborsGridSearch;
import com.jdistance.impl.workflow.gridsearch.clusterer.MinSpanningTreeGridSearch;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.competition.MetricTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.style.Smooth;
import jeigen.DenseMatrix;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ProcessingTest {
    @Before
    public void initContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        File contextFile = new File(classLoader.getResource("test_context.xml").getFile());
        ContextProvider.getInstance().useCustomContext(contextFile);

        File testFolder = new File(ContextProvider.getContext().getImgFolder());
        if (testFolder.exists()) {
            for (File file : testFolder.listFiles()) {
                file.delete();
            }
        }
        testFolder.mkdirs();
    }

    @Test
    public void testDrawSP_CTAttitude() {
        new TaskChain("validate SP-CT attitude", new MetricTask(new MetricWrapper(Metric.SP_CT), Constants.triangleGraph, 100, 0.0, 1.0))
                .execute().drawUnique("[0.0:1.0]", "0,0.2,1");
        String filePath = ContextProvider.getContext().getImgFolder() + "/validate SP-CT attitude.png";
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void testConstantResultClassifier() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = GnPInPOutGraphGenerator.getInstance().generate(properties);
        GridSearch gridSearch = new KNearestNeighborsGridSearch(bundle, 4, 0.3);
        Task task = new DefaultTask(gridSearch, new MetricWrapper(Metric.COMM30), 10);
        Map<Double, Double> result = new TaskChain("validate", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testConstantResultClusterer() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = GnPInPOutGraphGenerator.getInstance().generate(properties);
        GridSearch gridSearch = new MinSpanningTreeGridSearch(bundle, 4);
        Task task = new DefaultTask(gridSearch, new MetricWrapper(Metric.COMM30), 10);
        Map<Double, Double> result = new TaskChain("validate", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testBestClassifierResultNotNull() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = GnPInPOutGraphGenerator.getInstance().generate(properties);
        GridSearch gridSearch = new KNearestNeighborsGridSearch(bundle, 4, 0.3);
        List<Task> tasks = TaskChainBuilder.generateDefaultTasks(gridSearch, Metric.getDefaultDistances(), 10);
        tasks.forEach(i -> {
            Map.Entry<Double, Double> bestResult = i.getMaxResult();
            assertTrue("For " + i.getName() + " lambda = " + bestResult.getKey() + " best result - NaN", !bestResult.getValue().isNaN());
        });
    }

    @Test
    public void testTriangleInequality() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(1, 100, 5, 0.3, 0.1);
        GraphBundle bundle = GnPInPOutGraphGenerator.getInstance().generate(properties);
        Metric.getAll().forEach(metric -> bundle.getGraphs().forEach(graph -> {
            double countErrors = 0.0;
            for (double base = 0; base <= 1; base += 0.01) {
                DenseMatrix A = graph.getA();
                Double param = metric.getScale().calc(A, base);
                DenseMatrix D = metric.getD(graph.getA(), param);

                int size = D.rows;
                for (int i = 0; i < size; i++) {
                    for (int j = i+1; j < size; j++) {
                        for (int k = j+1; k < size; k++) {
                            double distIJ = D.get(i, j);
                            double distJK = D.get(j, k);
                            double distIK = D.get(i, k);
                            if (distIJ + distJK < distIK || distIJ + distIK < distJK || distJK + distIK < distIJ) {
                                countErrors++;
                            }
                        }
                    }
                }
            }
            System.out.println("\"" + metric.getName() + "\" has " + countErrors + " triangle inequality errors");
        }));
    }
}