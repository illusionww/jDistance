package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.workflow.Environment;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.checker.ClustererChecker;
import com.thesis.workflow.task.DefaultTask;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initEnvironment();

        Parser parser = new ParserWrapper();
        List<Graph> graphs1 = parser.parseInDirectory(Constants.GRAPH_FOLDER + Constants.FOLDER1);
        List<Graph> graphs2 = parser.parseInDirectory(Constants.GRAPH_FOLDER + Constants.FOLDER2);


        List<Distance> distances = new ArrayList<>();
        distances.add(Distance.WALK);
        distances.add(Distance.LOGARITHMIC_FOREST);
        distances.add(Distance.PLAIN_FOREST);
        distances.add(Distance.PLAIN_WALK);
        distances.add(Distance.COMMUNICABILITY);
        distances.add(Distance.LOGARITHMIC_COMMUNICABILITY);
        distances.add(Distance.COMBINATIONS);
        distances.add(Distance.HELMHOLTZ_FREE_ENERGY);

        new TaskChain(new DefaultTask(new ClustererChecker(graphs2, 5), distances, 0.01))
                .execute().draw("clusterer(k=5) " + Constants.FOLDER2_NAME);
    }

    public static void initEnvironment() {
        Environment.GNUPLOT_PATH = Constants.GNUPLOT_PATH;
        Environment.IMG_FOLDER = Constants.IMG_FOLDER;
        Environment.PARALLEL = Constants.PARALLEL;
    }
}

