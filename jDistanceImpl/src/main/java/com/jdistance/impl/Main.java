package com.jdistance.impl;

import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.graphgenerator.utils.GeneratorPropertiesParser;
import com.jdistance.graph.Graph;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.adapter.generator.dcr.DCRGeneratorAdapter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.KNearestNeighborsChecker;
import com.jdistance.impl.workflow.checker.MinSpanningTreeChecker;
import com.jdistance.impl.workflow.checker.WardChecker;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        drawGraphsScenarioOld();
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures";
        new File("./pictures").mkdir();
        context.COMPETITION_FOLDER = "tournament";
        new File("./tournament").mkdir();
        context.PARALLEL = true;
    }

    private static void drawGraphsScenarioOld() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 2;
        int numOfNodes = 200;
        double pIn = 0.3;
        double pOut = 0.1;
        int numOfClusters = 5;
        int numOfPoints = 40;

        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        List<Graph> graphs = generator.generateList(graphCount, numOfNodes, pIn, pOut, numOfClusters);
        GraphBundle graphBundle = new GraphBundle(new GeneratorPropertiesDTO(), graphs);
        ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphBundle, numOfClusters), Metric.getDefaultDistances(), numOfPoints).execute().draw();
        ScenarioHelper.defaultTasks(new WardChecker(graphBundle, numOfClusters), Metric.getDefaultDistances(), numOfPoints).execute().draw();
    }

    private static void compareClusterers() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 10;
        int numOfNodes = 250;
        double pIn = 0.30;
        double pOut = 0.05;
        int numOfClusters = 5;
        int numOfPoints = 100;

        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        List<Graph> graphs = generator.generateList(graphCount, numOfNodes, pIn, pOut, numOfClusters);
        GraphBundle graphBundle = new GraphBundle(new GeneratorPropertiesDTO(), graphs);

        TaskChain chain = new TaskChain("Compare Clusterers FE, logComm 0.35 0.03");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new DefaultTask(new WardChecker(graphBundle, numOfClusters), new MetricWrapper("Ward FE", Metric.FREE_ENERGY), numOfPoints));
        tasks.add(new DefaultTask(new WardChecker(graphBundle, numOfClusters), new MetricWrapper("Ward logComm", Metric.LOG_COMM_D), numOfPoints));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphBundle, numOfClusters), new MetricWrapper("Tree FE", Metric.FREE_ENERGY), numOfPoints));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphBundle, numOfClusters), new MetricWrapper("Tree logComm", Metric.LOG_COMM_D), numOfPoints));
        chain.addTasks(tasks).execute().draw();
    }

    private static void drawGraphsScenarioNew() {
        GeneratorPropertiesDTO properties = GeneratorPropertiesParser.parse("./dataForGenerator/defaultParameters.txt");
        GraphBundle graphBundle = new GraphBundle(properties, 10);
        ScenarioHelper.defaultTasks(new KNearestNeighborsChecker(graphBundle, 3, 0.3), Metric.getDefaultDistances(), 50).execute().draw();
    }
}

