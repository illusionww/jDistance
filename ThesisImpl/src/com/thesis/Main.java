package com.thesis;

import com.panayotis.gnuplot.utils.FileUtils;
import com.thesis.adapter.generator.DCRGeneratorAdapter;
import com.thesis.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.metric.Distances;
import com.thesis.metric.Scale;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        findBestClassifierParameterScenario();
    }

    private static void drawGraphsScenario() throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                Distances.SP_CT.getInstance(),
                Distances.FREE_ENERGY.getInstance(),
                Distances.WALK.getInstance(),
                Distances.LOG_FOREST.getInstance(),
                Distances.FOREST.getInstance(),
                Distances.PLAIN_WALK.getInstance(),
                Distances.COMMUNICABILITY.getInstance(),
                Distances.LOG_COMMUNICABILITY.getInstance()
        );

        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        List<Graph> graphs = generator.generateList(5, 100, 0.3, 0.1, 5);
        new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 3, 0.3), distances, 0.01))
                .execute().draw("classifier(k=3, p=0.3) count=50, n=100, p_in=0.3, p_out=0.1 exp");
    }

    private static void findBestClassifierParameterScenario() {
        List<Distances> distances = Arrays.asList(
                Distances.FREE_ENERGY,
                Distances.WALK,
                Distances.LOG_FOREST,
                Distances.FOREST,
                Distances.PLAIN_WALK,
                Distances.COMMUNICABILITY,
                Distances.LOG_COMMUNICABILITY
        );

        Arrays.asList(100, 300, 500).forEach(numOfNodes -> {
            try {
                DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
                List<Graph> graphs = generator.generateList(1, numOfNodes, 0.3, 0.02, 5);

                distances.forEach(distance -> {
                    log.info("Distance: {}", distance.getInstance().getShortName());
                    List<Task> tasks = new ArrayList<>();
                    IntStream.range(1, 3).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                            distance.getInstance(Integer.toString(i)), 0.0, 3.0, 0.1, 0.1)));
                    try {
                        new TaskChain(tasks).execute().draw("bestParam " + distance.getInstance().getShortName() + " (k=0..10, p=0.3) count=5, n=" + numOfNodes + ", p_in=0.3, p_out=0.02");
                    } catch (RuntimeException e) {
                        log.error("Exception while execute/draw", e);
                    }
                });
            } catch (IOException | ParserConfigurationException | SAXException e) {
                log.error("Exception while graph creation", e);
            }
        });
    }

    private static void initContext() {
        String gnuplotPath = "c:\\cygwin64\\bin\\gnuplot.exe";
        String imgFolder = "pictures";

        gnuplotPath = isExist(gnuplotPath) ? gnuplotPath :FileUtils.findPathExec();

        if (!isExist(imgFolder) && !new File(imgFolder).mkdirs()) {
            throw new RuntimeException("Folder " + new File(imgFolder).getAbsolutePath() + " is not exist");
        }

        if (!isExist(gnuplotPath)) {
            throw new RuntimeException("Gnuplot not found");
        }

        Context.getInstance().init(gnuplotPath, imgFolder, true, Scale.EXP);
    }

    private static boolean isExist(String path) {
        return path != null && new File(path).exists();
    }
}

