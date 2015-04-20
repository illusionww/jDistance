package com.thesis;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        drawGraphsScenario();
    }

    private static void drawGraphsScenario() throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                DistanceClass.SP_CT.getInstance(),
                DistanceClass.FREE_ENERGY.getInstance(),
                DistanceClass.WALK.getInstance(),
                DistanceClass.LOG_FOREST.getInstance(),
                DistanceClass.FOREST.getInstance(),
                DistanceClass.PLAIN_WALK.getInstance(),
                DistanceClass.COMMUNICABILITY.getInstance(),
                DistanceClass.LOG_COMMUNICABILITY.getInstance()
        );

        Arrays.asList(5).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    Scenario.defaultTasks(new ClassifierChecker(graphs, 3, 0.3), distances, 100).execute().draw();
                });
            });
        });
    }

    private static void findBestClassifierParameterScenario() {
        List<DistanceClass> distances = Arrays.asList(
//                DistanceClass.SP_CT,
//                DistanceClass.FREE_ENERGY,
//                DistanceClass.WALK,
//                DistanceClass.LOG_FOREST,
//                DistanceClass.FOREST,
//                DistanceClass.PLAIN_WALK,
//                DistanceClass.COMMUNICABILITY,
                DistanceClass.LOG_COMMUNICABILITY
        );

        Arrays.asList(3).forEach(graphCount -> {
            Arrays.asList(100).forEach(numOfNodes -> {
                Arrays.asList(0.02, 0.05, 0.1).forEach(pOut -> {
                    GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, pOut, 5, graphCount);
                    distances.forEach(distanceClass -> {
                        List<Task> tasks = new ArrayList<>();
                        IntStream.range(1, 8).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                                distanceClass.getInstance(Integer.toString(i)), 0.0, 1.5, 0.05, 100)));
                        String taskChainName = "bestParam " + distanceClass.getInstance().getName() + " n=" + numOfNodes + ", p_i=0.3, p_o=" + pOut + ", count=" + graphCount;
                        new TaskChain(taskChainName, tasks).execute().draw();
                    });
                });
            });
        });
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures";
        context.CACHE_FOLDER = "cache";
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }
}

