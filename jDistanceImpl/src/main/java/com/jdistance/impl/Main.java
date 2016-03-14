package com.jdistance.impl;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.ClusteredGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.impl.adapter.graph.CSVReader;
import com.jdistance.impl.adapter.graph.DCRGeneratorAdapter;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.clusterer.MinSpanningTreeChecker;
import com.jdistance.impl.workflow.checker.clusterer.WardChecker;
import com.jdistance.impl.workflow.checker.nolearning.DiffusionChecker;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
        drawCSVGraphsPoliticalBooks();
        drawCSVGraphsFootball();
    }

    private static void compareClusterers() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 10;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs1 = ClusteredGraphGenerator.getInstance().generate(properties);
        GraphBundle graphs2 = graphs1.clone();

//        new TaskChain("MinSpanningTree: 200 nodes, 5 clusters, 10 graphs, pIn=0.30, pOut=0.05")
//                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs1, clustersCount), Metric.getDefaultDistances(), pointsCount))
//                .execute().draw().write();

        new TaskChain("Ward: 200 nodes, 5 clusters, 10 graphs, pIn=0.30, pOut=0.05")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs2, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
    }

    private static void drawGraphsScenarioOld() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 2;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.3;
        double pOut = 0.03;
        int pointsCount = 40;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = DCRGeneratorAdapter.getInstance().generate(properties);
        TaskChain chain = new TaskChain("");
        chain.addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();
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

    private static void compareGenerators() {
        int graphCount = 10;
        int nodesCount = 250;
        int clustersCount = 5;
        double pIn = 0.3;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphsOldGen = DCRGeneratorAdapter.getInstance().generate(properties);
        GraphBundle graphsNewGen = ClusteredGraphGenerator.getInstance().generate(properties);

        TaskChain chain = new TaskChain("Compare generators logComm 0.3 0.05");
        List<Task> tasks = new ArrayList<>();
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphsOldGen, clustersCount), new MetricWrapper("Tree logComm old gen", Metric.LOG_COMM_D), pointsCount));
        tasks.add(new DefaultTask(new MinSpanningTreeChecker(graphsNewGen, clustersCount), new MetricWrapper("Tree logComm new gen", Metric.LOG_COMM_D), pointsCount));
        chain.addTasks(tasks).execute().draw();
    }

    private static void compareGeneratorGraphs() throws SAXException, IOException, TransformerConfigurationException {
        int graphCount = 1;
        int nodesCount = 250;
        int clustersCount = 5;
        double pIn = 0.3;
        double pOut = 0.05;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphsOldGen = DCRGeneratorAdapter.getInstance().generate(properties);
        GraphBundle graphsNewGen = ClusteredGraphGenerator.getInstance().generate(properties);

        GraphMLWriter writer = new GraphMLWriter();
        writer.writeGraph(graphsOldGen.getGraphs().get(0), "old.graphml");
        writer.writeGraph(graphsNewGen.getGraphs().get(0), "new.graphml");
    }

    private static void drawCSVGraphsPoliticalBooks() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        int pointsCount = 200;

        Graph graph = new CSVReader().importGraph("data/polbooks_nodes.csv", "data/polbooks_edges.csv");
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(1, 105, 3, 0, 0);
        GraphBundle graphs = new GraphBundle(new ArrayList<Graph>() {{
            add(graph);
        }}, properties);

//        new TaskChain("Polbooks: 105 nodes, 3 clusters, Ward")
//                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 3), Metric.getDefaultDistances(), pointsCount))
//                .execute().draw().write();
//
//        new TaskChain("Polbooks: 105 nodes, 3 clusters, MinSpanningTree")
//                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 3), Metric.getDefaultDistances(), pointsCount))
//                .execute().draw().write();

                new TaskChain("Polbooks: 105 nodes, 3 clusters, Diffusion")
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();

//        GraphMLWriter writer = new GraphMLWriter();
//        writer.writeGraph(graphs.getGraphs().get(0), ContextProvider.getInstance().getContext().getImgFolder() + "/polbooks.graphml");
    }

    private static void drawCSVGraphsFootball() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        int pointsCount = 200;

        Graph graph = new CSVReader().importGraph("data/football_nodes.csv", "data/football_edges.csv");
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(1, 115, 12, 0, 0);
        GraphBundle graphs = new GraphBundle(new ArrayList<Graph>() {{
            add(graph);
        }}, properties);

//        new TaskChain("Football: 115 nodes, 12 clusters, Ward")
//                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 12), Metric.getDefaultDistances(), pointsCount))
//                .execute().draw().write();
//
//        new TaskChain("Football: 115 nodes, 12 clusters, MinSpanningTree")
//                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 12), Metric.getDefaultDistances(), pointsCount))
//                .execute().draw().write();

        new TaskChain("Football: 115 nodes, 12 clusters, Diffusion")
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();

//        GraphMLWriter writer = new GraphMLWriter();
//        writer.writeGraph(graphs.getGraphs().get(0), ContextProvider.getInstance().getContext().getImgFolder() + "/football.graphml");
    }

    private static void drawDiffusionGraph01() {
        int graphCount = 3;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = ClusteredGraphGenerator.getInstance().generate(properties);

        new TaskChain("MinSpanningTree: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
        new TaskChain("Diffusion: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
        new TaskChain("Ward: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
    }

    private static void drawDiffusionGraph005() {
        int graphCount = 5;
        int nodesCount = 100;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = ClusteredGraphGenerator.getInstance().generate(properties);

        new TaskChain("Ward: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
        new TaskChain("Diffusion: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
    }

    private static void drawDiffusionGraph20001() {
        int graphCount = 2;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.10;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = ClusteredGraphGenerator.getInstance().generate(properties);

        new TaskChain("Ward: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
        new TaskChain("Diffusion: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
    }

    private static void drawDiffusionGraph200005() {
        int graphCount = 2;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.05;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs = ClusteredGraphGenerator.getInstance().generate(properties);

        new TaskChain("Ward: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, clustersCount), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
        new TaskChain("Diffusion: " + nodesCount + " nodes, " + clustersCount + " clusters, " + graphCount + " graphs, pIn=" + pIn + ", pOut=" + pOut)
                .addTasks(ScenarioHelper.defaultTasks(new DiffusionChecker(graphs), Metric.getDefaultDistances(), pointsCount))
                .execute().draw().write();
    }
}

