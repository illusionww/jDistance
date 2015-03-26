package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.graph.Graph;
import com.thesis.helper.Constants;
import com.thesis.helper.MetricTask;
import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import jeigen.DenseMatrix;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class ProcessingTest {
    private List<Graph> graphs;
    private List<Distance> distances;

    @Before
    public void prepare() throws IOException, ParserConfigurationException, SAXException {
        Context.getInstance().init(Constants.GNUPLOT_PATH, Constants.IMG_FOLDER, true, Scale.ATAN);

        distances = Arrays.asList(Distance.values());

        Parser parser = new ParserWrapper();
        graphs = new ArrayList<>();

        URL url = Thread.currentThread().getContextClassLoader().getResource(Constants.GRAPHML_EXAMPLE1);
        graphs.add(parser.parse(url.getPath()));
    }

    @Test
    public void testClassifierParallel() {
        Task task1 = new DefaultTask(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);
        Task task2 = new DefaultTask(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);

        Context.getInstance().PARALLEL = false;
        Map<Distance, Map<Double, Double>> notParallel = new TaskChain(task1).execute().draw("notParallel").getData().get(task1);
        Context.getInstance().PARALLEL = true;
        Map<Distance, Map<Double, Double>> parallel = new TaskChain(task2).execute().draw("parallel").getData().get(task2);

        distances.forEach(distance -> {
            Map<Double, Double> notParallelPoints = notParallel.get(distance);
            Map<Double, Double> parallelPoints = parallel.get(distance);

            notParallelPoints.keySet().forEach(x -> assertTrue("Parallel calculation not working for " + distance.getName() + " " + x + ": " + notParallelPoints.get(x) + " != " + parallelPoints.get(x),
                    Objects.equals(notParallelPoints.get(x), parallelPoints.get(x))));
        });
    }

    @Test
    public void drawSP_CTAttitude() {
        DenseMatrix triangleGraph = new DenseMatrix(new double[][]{
                {0, 1, 0, 0},
                {1, 0, 1, 1},
                {0, 1, 0, 1},
                {0, 1, 1, 0}
        });

        new TaskChain()
                .addTask(new MetricTask(Distance.COMBINATIONS, triangleGraph, 0.01))
                .execute().draw("SP-CT");
    }
}
