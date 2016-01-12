package com.jdistance.impl;

import com.graphgenerator.utils.GeneratorPropertiesDTO;
import com.graphgenerator.utils.GeneratorPropertiesParser;
import com.jdistance.graph.Graph;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.adapter.generator.dcr.DCRGeneratorAdapter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.checker.ClassifierChecker;
import com.jdistance.metric.Metric;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
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

    private static void drawGraphsScenarioOld() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 10;
        int numOfNodes = 250;
        double pIn = 0.3;
        double pOut = 0.07;
        int numOfClusters = 5;

        DCRGeneratorAdapter generator = new DCRGeneratorAdapter();
        List<Graph> graphs = generator.generateList(graphCount, numOfNodes, pIn, pOut, numOfClusters);
        GraphBundle graphBundle = new GraphBundle(new GeneratorPropertiesDTO(), graphs);
        ScenarioHelper.defaultTasks(new ClassifierChecker(graphBundle, 3, 0.3), Metric.getDefaultDistances(), 50).execute().draw();
    }

    private static void drawGraphsScenarioNew() {
        GeneratorPropertiesDTO properties = GeneratorPropertiesParser.parse("./dataForGenerator/defaultParameters.txt");
        GraphBundle graphBundle = new GraphBundle(properties, 10);
        ScenarioHelper.defaultTasks(new ClassifierChecker(graphBundle, 3, 0.3), Metric.getDefaultDistances(), 50).execute().draw();
    }
}

