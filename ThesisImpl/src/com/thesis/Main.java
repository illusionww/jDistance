package com.thesis;

import com.thesis.adapter.generator.DCRGeneratorAdapter;
import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.Distances;
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
//                Distances.SP_CT.getInstance(),
//                Distances.FREE_ENERGY.getInstance(),
//                Distances.WALK.getInstance(),
//                Distances.LOG_FOREST.getInstance(),
//                Distances.FOREST.getInstance(),
//                Distances.PLAIN_WALK.getInstance(),
//                Distances.COMMUNICABILITY.getInstance(),
                Distances.LOG_COMMUNICABILITY.getInstance()
        );

        GraphBundle graphs = new GraphBundle(100, 0.3, 0.1, 5, 5);
        Scenario.defaultTasks(new ClassifierChecker(graphs, 3, 0.3), distances, 0.01).execute().draw();
    }

    private static void findBestClassifierParameterScenario() {
        List<Distances> distances = Arrays.asList(
                Distances.SP_CT,
                Distances.FREE_ENERGY,
                Distances.WALK,
                Distances.LOG_FOREST,
                Distances.FOREST,
                Distances.PLAIN_WALK,
                Distances.COMMUNICABILITY,
                Distances.LOG_COMMUNICABILITY
        );

        int graphCount = 30;

        Arrays.asList(200).forEach(numOfNodes -> {
            Arrays.asList(0.02, 0.05, 0.1).forEach(p_out -> {
                GraphBundle graphs = new GraphBundle(numOfNodes, 0.3, p_out, 5, graphCount);
                distances.forEach(distance -> {
                    log.info("Distance: {}", distance.getInstance().getShortName());
                    List<Task> tasks = new ArrayList<>();
                    IntStream.range(1, 8).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                            distance.getInstance(Integer.toString(i)), 0.0, 10.0, 0.1, 0.1)));
                    try {
                        new TaskChain(tasks).execute().draw("bestParam " + distance.getInstance().getShortName() + " n=" + numOfNodes + ", p_i=0.3, p_o=" + p_out + ", count=" + graphCount + " (k=1..7, p=0.3)");
                    } catch (RuntimeException e) {
                        log.error("Exception while execute/draw", e);
                    }
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
        context.SCALE = Scale.EXP;
    }
}

