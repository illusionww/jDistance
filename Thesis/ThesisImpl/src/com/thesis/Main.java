package com.thesis;

import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.ParserWrapper;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.workflow.Environment;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.DefaultTask;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initEnvironment();

        Parser parser = new ParserWrapper();
        List<Graph> graphs = parser.parseInDirectory(Constants.GRAPH_FOLDER + Constants.FOLDER1);

        new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 10, 0.3), Arrays.asList(Distance.values()), 0.01))
                .execute(true).draw(Constants.FOLDER1_NAME);
    }

    public static void initEnvironment() {
        Environment.GNUPLOT_PATH = Constants.GNUPLOT_PATH;
        Environment.IMG_FOLDER = Constants.IMG_FOLDER;
    }
}

