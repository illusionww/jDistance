package com.jdistance;

import com.graphgenerator.utils.GeneratorPropertiesParser;
import com.jdistance.helper.Constants;
import com.jdistance.impl.ScenarioHelper;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.ClassifierChecker;
import com.jdistance.impl.workflow.checker.ClustererChecker;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.MetricTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ProcessingTest {
    @Test
    public void testDrawSP_CTAttitude() {
        new TaskChain("test SP-CT attitude", new MetricTask(new MetricWrapper(Metric.SP_CT), Constants.triangleGraph, 100, 0.0, 1.0))
                .execute().draw();
        String filePath = Context.getInstance().IMG_FOLDER + "/SP-CT.png";
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void testConstantResultClassifier() {
        GraphBundle bundle = new GraphBundle(GeneratorPropertiesParser.parse("./dataForGenerator/defaultParameters.txt"), 2);
        Checker checker = new ClassifierChecker(bundle, 4, 0.3);
        Task task = new DefaultTask(checker, new MetricWrapper(Metric.COMM), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testConstantResultClusterer() {
        GraphBundle bundle = new GraphBundle(GeneratorPropertiesParser.parse("./dataForGenerator/defaultParameters.txt"), 2);
        Checker checker = new ClustererChecker(bundle, 4);
        Task task = new DefaultTask(checker, new MetricWrapper(Metric.COMM), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testBestClassifierResultNotNull() {
        GraphBundle bundle = new GraphBundle(GeneratorPropertiesParser.parse("./dataForGenerator/defaultParameters.txt"), 5);
        Checker checker = new ClassifierChecker(bundle, 4, 0.3);
        TaskChain chain = ScenarioHelper.defaultTasks(checker, Metric.getAll().stream().map(MetricWrapper::new).collect(Collectors.toList()), 10);
        List<Task> result = chain.execute().getTasks();
        result.forEach(i -> {
            Map.Entry<Double, Double> bestResult = i.getBestResult();
            assertTrue("For " + i.getName() + " lambda = " + bestResult.getKey() + " best result - NaN", !bestResult.getValue().isNaN());
        });
    }
}