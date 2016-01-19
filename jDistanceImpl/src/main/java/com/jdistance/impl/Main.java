package com.jdistance.impl;

import com.jdistance.graph.generator.ClusteredGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.dcrgenerator.DCRGeneratorAdapter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.TaskChain;
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
        drawGraphsScenarioNew();
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

    private static void compareClusterers() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 10;
        int nodesCount = 250;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = DCRGeneratorAdapter.getInstance().generate(properties);

        TaskChain chain = new TaskChain("Compare Clusterers FE, logComm 0.35 0.03");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new DefaultTask(new WardChecker(graphs, clustersCount), new MetricWrapper("Ward FE", Metric.FREE_ENERGY), pointsCount));
        tasks.add(new DefaultTask(new WardChecker(graphs, clustersCount), new MetricWrapper("Ward logComm", Metric.LOG_COMM_D), pointsCount));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphs, clustersCount), new MetricWrapper("Tree FE", Metric.FREE_ENERGY), pointsCount));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphs, clustersCount), new MetricWrapper("Tree logComm", Metric.LOG_COMM_D), pointsCount));
        chain.addTasks(tasks).execute().draw();
    }

    private static void drawGraphsScenarioOld() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 2;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.3;
        double pOut = 0.1;
        int pointsCount = 40;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = DCRGeneratorAdapter.getInstance().generate(properties);
        ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount).execute().draw();
    }

    private static void drawGraphsScenarioNew() {
        int graphCount = 2;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.3;
        double pOut = 0.1;
        int pointsCount = 40;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = ClusteredGraphGenerator.getInstance().generate(properties);

        TaskChain chain = new TaskChain("Compare Clusterers (new generator) logComm 0.35 0.03");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new DefaultTask(new WardChecker(graphs, clustersCount), new MetricWrapper("Ward FE", Metric.FREE_ENERGY), pointsCount));
        tasks.add(new DefaultTask(new WardChecker(graphs, clustersCount), new MetricWrapper("Ward logComm", Metric.LOG_COMM_D), pointsCount));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphs, clustersCount), new MetricWrapper("Tree FE", Metric.FREE_ENERGY), pointsCount));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphs, clustersCount), new MetricWrapper("Tree logComm", Metric.LOG_COMM_D), pointsCount));
        chain.addTasks(tasks).execute().draw();
    }
}

