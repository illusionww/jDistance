package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.helper.Constants;
import com.thesis.metric.Distance;
import com.thesis.workflow.Environment;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class CheckerTest {
    private List<Graph> graphs;
    private List<Distance> distances;

    @Before
    public void prepare() throws IOException {
        Environment.GNUPLOT_PATH = Constants.GNUPLOT_PATH;
        Environment.IMG_FOLDER = Constants.IMG_FOLDER;

        distances = Arrays.asList(Distance.values());

        Parser parser = new ParserWrapper();
        graphs = parser.parseInDirectory(Constants.GRAPH_FOLDER + Constants.FOLDER1);
    }

    @Test
    public void testClassifierParallel() {
        Task task1 = new DefaultTask(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);
        Task task2 = new DefaultTask(new ClassifierChecker(graphs, 1, 0.3), distances, 0.1);

        Environment.PARALLEL = false;
        Map<Distance, Map<Double, Double>> notParallel = new TaskChain(task1).execute().draw("notParallel").getData().get(task1);
        Environment.PARALLEL = true;
        Map<Distance, Map<Double, Double>> parallel = new TaskChain(task2).execute().draw("parallel").getData().get(task2);

        distances.forEach(distance -> {
            Map<Double, Double> notParallelPoints = notParallel.get(distance);
            Map<Double, Double> parallelPoints = parallel.get(distance);

            notParallelPoints.keySet().forEach(x -> assertTrue("Parallel calculation not working for " + distance.getName() + " " + x + ": " + notParallelPoints.get(x) + " != " + parallelPoints.get(x),
                    Objects.equals(notParallelPoints.get(x), parallelPoints.get(x))));
        });
    }
}
