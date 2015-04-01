package com.thesis;

import com.panayotis.gnuplot.utils.FileUtils;
import com.thesis.adapter.generator.DCRGeneratorAdapter;
import com.thesis.graph.Graph;
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

        List<Distances> distances = Arrays.asList(
                Distances.WALK,
                Distances.LOG_FOREST,
                Distances.SP_CT,
                Distances.FREE_ENERGY,
                Distances.FOREST,
                Distances.PLAIN_WALK,
                Distances.COMMUNICABILITY,
                Distances.LOG_COMMUNICABILITY
        );

        Arrays.asList(100, 300, 500).forEach(numOfNodes -> {
            try {
                DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
                List<Graph> graphs = generator.generateList(5, numOfNodes, 0.3, 0.1, 5);

                distances.forEach(distance -> {
                    log.info("Distance: {}" + distance.getInstance().getShortName());
                    List<Task> tasks = new ArrayList<>();
                    IntStream.range(1, 10).forEach(i -> tasks.add(new ClassifierBestParamTask(new ClassifierChecker(graphs, i, 0.3),
                            Collections.singletonList(distance.getInstance(Integer.toString(i))), 0.0, 10.0, 0.05, 0.05)));
                    new TaskChain(tasks).execute().draw("bestParam " + distance.getInstance().getShortName() + " (k=0..10, p=0.3) count=10, n=100, p_in=0.3, p_out=0.1");
                });
            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
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

