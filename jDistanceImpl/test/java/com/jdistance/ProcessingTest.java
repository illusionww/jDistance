package com.jdistance;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.adapter.parser.Parser;
import com.jdistance.adapter.parser.ParserWrapper;
import com.jdistance.cache.CacheManager;
import com.jdistance.cache.CacheUtils;
import com.jdistance.helper.Constants;
import com.jdistance.workflow.task.MetricTask;
import com.jdistance.helper.TestHelperImpl;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.TaskChain;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.checker.ClassifierChecker;
import com.jdistance.workflow.checker.ClustererChecker;
import com.jdistance.workflow.task.DefaultTask;
import com.jdistance.workflow.task.Task;
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
    public void testCacheManager() throws IOException {
        Parser parser = new ParserWrapper();
        URL url = Thread.currentThread().getContextClassLoader().getResource(Constants.n100pin03pout01k5FOLDER);
        GraphBundle graphs = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));
        GraphBundle graphs1 = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));
        graphs1.setGraphs(graphs1.getGraphs().subList(2, 4));
        GraphBundle graphs2 = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));

        TaskChain chain = ScenarioHelper.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 10);
        TaskChain chain1 = ScenarioHelper.defaultTasks(new ClassifierChecker(graphs1, 1, 0.3), distances, 10);
        TaskChain chain2 = ScenarioHelper.defaultTasks(new ClassifierChecker(graphs2, 1, 0.3), distances, 10);

        Context.getInstance().USE_CACHE = false;
        Map<Distance, Map<Double, Double>> withoutCache = TestHelperImpl.toDistanceMap(chain.execute().getData());
        Context.getInstance().USE_CACHE = true;
        chain1.execute();

        int cacheCount = CacheUtils.getAllSerFiles(Context.getInstance().CACHE_FOLDER).size();
        assertTrue("cache files is not created", cacheCount > 0);

        CacheManager.getInstance().reconciliation();

        Map<Distance, Map<Double, Double>> withCache = TestHelperImpl.toDistanceMap(chain2.execute().getData());

        distances.forEach(distance -> {
            Map<Double, Double> withoutCachePoints = withoutCache.get(distance);
            Map<Double, Double> withCachePoints = withCache.get(distance);

            withoutCachePoints.keySet().forEach(x -> assertTrue("Calculation with cache not working for " + distance.getName() + " " + x + ": " + withoutCachePoints.get(x) + " != " + withCachePoints.get(x),
                    TestHelperImpl.equalDoubleStrict(withoutCachePoints.get(x), withCachePoints.get(x))));
        });
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
        List<Task>  result = chain.execute().getTasks();
        result.forEach(i -> {
            Map.Entry<Double, Double> bestResult = i.getBestResult();
            assertTrue("For " + i.getName() + " lambda = " + bestResult.getKey() + " best result - NaN", !bestResult.getValue().isNaN());
        });
    }
}
