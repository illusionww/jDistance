package com.thesis;

import com.thesis.adapter.parser.GraphMLParser;
import com.thesis.adapter.parser.Parser;
import com.thesis.adapter.parser.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.Environment;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.ClassifierChecker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initEnvironment();

        Parser parser = new GraphMLParser();
        List<Graph> graphs = new ArrayList<>();
        graphs.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_1));
        graphs.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_2));
        graphs.add(parser.parse(Constants.GRAPH_FOLDER + Constants.SIMPLEGRAPH_1_3));

        new TaskChain(new DefaultTask(new ClassifierChecker(graphs, 10, 0.3), Arrays.asList(Distance.values()), 0.02))
                .execute().draw("classifier n=100 k=10 p=0.3");
    }

    public static void initEnvironment() {
        Environment.GNUPLOT_PATH = Constants.GNUPLOT_PATH;
        Environment.IMG_FOLDER = Constants.IMG_FOLDER;
    }
}

