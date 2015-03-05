package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.helper.Constants;
import com.thesis.metric.Distance;
import com.thesis.workflow.Environment;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class ParallelTest {
    List<Graph> graphs;

    @Before
    public void prepareGraph() throws IOException, SAXException, ParserConfigurationException {
        Environment.GNUPLOT_PATH = Constants.GNUPLOT_PATH;
        Environment.IMG_FOLDER = Constants.IMG_FOLDER;

        Parser parser = new ParserWrapper();
        graphs = parser.parseInDirectory(Constants.GRAPH_FOLDER + Constants.FOLDER1);
    }

    @Test
    public void testParallelDistances() {
        Task task1 = new DefaultTask(new ClassifierChecker(graphs, 10, 0.3), Arrays.asList(Distance.values()), 0.1);
        Task task2 = new DefaultTask(new ClassifierChecker(graphs, 10, 0.3), Arrays.asList(Distance.values()), 0.1);

        Map<Distance, Map<Double, Double>> notParallel = new TaskChain(task1).execute(false).draw("notParallel").getData().get(task1);
        Map<Distance, Map<Double, Double>> parallel = new TaskChain(task2).execute(true).draw("parallel").getData().get(task2);

        Arrays.asList(Distance.values()).forEach(distance -> {
            Map<Double, Double> notParallelPoints = notParallel.get(distance);
            Map<Double, Double> parallelPoints = parallel.get(distance);

            notParallelPoints.keySet().forEach(x -> assertTrue("Parallel calculation not working for " + distance.getName() + " " + x + ": " + notParallelPoints.get(x) + " != " + parallelPoints.get(x),
                    Objects.equals(notParallelPoints.get(x), parallelPoints.get(x))));
        });
    }
}
