package com.thesis;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.cache.CacheManager;
import com.thesis.helper.Constants;
import com.thesis.helper.MetricTask;
import com.thesis.helper.TestHelperImpl;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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

        TaskChain chain1 = Scenario.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);
        TaskChain chain2 = Scenario.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);

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
    public void drawSP_CTAttitude() {
        new TaskChain("SP-CT").addTask(new MetricTask(DistanceClass.SP_CT.getInstance(), Constants.triangleGraph, 0.01))
                .execute().draw();
    }

    @Test
    public void testCacheManager() throws IOException {
        Parser parser = new ParserWrapper();
        URL url = Thread.currentThread().getContextClassLoader().getResource(Constants.n100pin03pout01k5FOLDER);
        GraphBundle graphs = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));
        GraphBundle graphs1 = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));
        graphs1.setGraphs(graphs1.getGraphs().subList(5, 10));
        GraphBundle graphs2 = new GraphBundle(100, 0.3, 0.1, 5, parser.parseInDirectory(url.getPath().substring(1)));

        TaskChain chain = Scenario.defaultTasks(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);
        TaskChain chain1 = Scenario.defaultTasks(new ClassifierChecker(graphs1, 1, 0.3), distances, 0.1);
        TaskChain chain2 = Scenario.defaultTasks(new ClassifierChecker(graphs2, 1, 0.3), distances, 0.1);

        Context.getInstance().USE_CACHE = false;
        Map<Distance, Map<Double, Double>> withoutCache = TestHelperImpl.toDistanceMap(chain.execute().getData());
        Context.getInstance().USE_CACHE = true;
        chain1.execute();
        CacheManager.getInstance().reconciliation();
        Map<Distance, Map<Double, Double>> withCache = TestHelperImpl.toDistanceMap(chain2.execute().getData());

        distances.forEach(distance -> {
            Map<Double, Double> withoutCachePoints = withoutCache.get(distance);
            Map<Double, Double> withCachePoints = withCache.get(distance);

            withoutCachePoints.keySet().forEach(x -> assertTrue("Calculation with cache not working for " + distance.getName() + " " + x + ": " + withoutCachePoints.get(x) + " != " + withCachePoints.get(x),
                    Objects.equals(withoutCachePoints.get(x), withCachePoints.get(x))));
        });
    }
}
