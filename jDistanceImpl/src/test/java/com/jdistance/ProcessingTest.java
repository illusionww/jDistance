package com.jdistance;

import com.jdistance.helper.Constants;
import com.jdistance.helper.TestHelperImpl;
import com.jdistance.impl.ScenarioHelper;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.adapter.parser.Parser;
import com.jdistance.impl.adapter.parser.ParserWrapper;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.checker.ClassifierChecker;
import com.jdistance.impl.workflow.checker.ClustererChecker;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.MetricTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class ProcessingTest {
    private List<Distance> distances;

    @Before
    public void prepare() {
        TestHelperImpl.initTestContext();

        distances = new ArrayList<>();
        Arrays.asList(DistanceClass.values()).stream().forEach(value -> distances.add(value.getInstance()));
    }

    @Test
    public void testClassifierParallel() throws IOException, SAXException, ParserConfigurationException {
        Parser parser = new ParserWrapper();
        URL url = Thread.currentThread().getContextClassLoader().getResource(Constants.GRAPHML_EXAMPLE1);
        GraphBundle graphs = new GraphBundle(null, null, null, null, Collections.singletonList(parser.parse(url.getPath())));

        TaskChain chain1 = ScenarioHelper.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 10);
        TaskChain chain2 = ScenarioHelper.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 10);

        Context.getInstance().PARALLEL = false;
        Map<Distance, Map<Double, Double>> notParallel = TestHelperImpl.toDistanceMap(chain1.execute().getData());
        Context.getInstance().PARALLEL = true;
        Map<Distance, Map<Double, Double>> parallel = TestHelperImpl.toDistanceMap(chain2.execute().getData());

        distances.forEach(distance -> {
            Map<Double, Double> notParallelPoints = notParallel.get(distance);
            Map<Double, Double> parallelPoints = parallel.get(distance);

            notParallelPoints.keySet().forEach(x -> assertTrue("Parallel calculation not working for " + distance.getName() + " " + x + ": " + notParallelPoints.get(x) + " != " + parallelPoints.get(x),
                    Objects.equals(notParallelPoints.get(x), parallelPoints.get(x))));
        });
    }

    @Test
    public void testDrawSP_CTAttitude() {
        new TaskChain("test SP-CT attitude", new MetricTask(DistanceClass.SP_CT.getInstance(), Constants.triangleGraph, 100, 0.0, 1.0))
                .execute().draw();
        String filePath = Context.getInstance().IMG_FOLDER + "/SP-CT.png";
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void testConstantResultClassifier() {
        GraphBundle bundle = new GraphBundle(100, 0.3, 0.1, 5, 2);
        Checker checker = new ClassifierChecker(bundle, 3, 0.3);
        Task task = new DefaultTask(checker, DistanceClass.COMM.getInstance(), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testConstantResultClusterer() {
        GraphBundle bundle = new GraphBundle(100, 0.3, 0.1, 5, 2);
        Checker checker = new ClustererChecker(bundle, 5);
        Task task = new DefaultTask(checker, DistanceClass.COMM.getInstance(), 10);
        Map<Double, Double> result = new TaskChain("test", Collections.singletonList(task)).execute().getData().get(task);
        long countDistinct = result.entrySet().stream().mapToDouble(Map.Entry::getValue).distinct().count();
        assertTrue("countDistinct should be > 1, but it = " + countDistinct, countDistinct > 1);
    }

    @Test
    public void testBestClassifierResultNotNull() {
        GraphBundle bundle = new GraphBundle(100, 0.3, 0.1, 5, 10);
        Checker checker = new ClassifierChecker(bundle, 3, 0.3);
        TaskChain chain = ScenarioHelper.defaultTasks(checker, DistanceClass.getAll().stream().map(DistanceClass::getInstance).collect(Collectors.toList()), 10);
        List<Task> result = chain.execute().getTasks();
        result.forEach(i -> {
            Map.Entry<Double, Double> bestResult = i.getBestResult();
            assertTrue("For " + i.getName() + " lambda = " + bestResult.getKey() + " best result - NaN", !bestResult.getValue().isNaN());
        });
    }
}
