package com.jdistance;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.ClusteredGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.impl.ScenarioHelper;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.KNearestNeighborsChecker;
import com.jdistance.impl.workflow.checker.MinSpanningTreeChecker;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.MetricTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
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

        File testFolder = new File(ContextProvider.getInstance().getContext().getImgFolder());
        if (testFolder.exists()) {
            for (File file : testFolder.listFiles()) {
                file.delete();
            }
        }
        testFolder.mkdirs();
    }

    @Test
    public void testDrawSP_CTAttitude() {
        new TaskChain("test SP-CT attitude", new MetricTask(new MetricWrapper(Metric.SP_CT), Constants.triangleGraph, 100, 0.0, 1.0))
                .execute().draw();
        String filePath = ContextProvider.getInstance().getContext().getImgFolder() + "/test SP-CT attitude.png";
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void testConstantResultClassifier() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = ClusteredGraphGenerator.getInstance().generate(properties);
        Checker checker = new KNearestNeighborsChecker(bundle, 4, 0.3);
        Task task = new DefaultTask(checker, new MetricWrapper(Metric.COMM_D), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testConstantResultClusterer() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = ClusteredGraphGenerator.getInstance().generate(properties);
        Checker checker = new MinSpanningTreeChecker(bundle, 4);
        Task task = new DefaultTask(checker, new MetricWrapper(Metric.COMM_D), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testBestClassifierResultNotNull() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(2, 200, 5, 0.3, 0.1);
        GraphBundle bundle = ClusteredGraphGenerator.getInstance().generate(properties);
        Checker checker = new KNearestNeighborsChecker(bundle, 4, 0.3);
        List<Task> tasks = ScenarioHelper.defaultTasks(checker, Metric.getDefaultDistances(), 10);
        tasks.forEach(i -> {
            Map.Entry<Double, Double> bestResult = i.getBestResult();
            assertTrue("For " + i.getName() + " lambda = " + bestResult.getKey() + " best result - NaN", !bestResult.getValue().isNaN());
        });
    }
}