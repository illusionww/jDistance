package com.jdistance.impl;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.ClusteredGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.impl.adapter.graph.CSVReader;
import com.jdistance.impl.adapter.graph.DCRGeneratorAdapter;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.MinSpanningTreeChecker;
import com.jdistance.impl.workflow.checker.WardChecker;
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
        compareClusterers();
    }

    private static void compareClusterers() throws ParserConfigurationException, SAXException, IOException {
        int graphCount = 10;
        int nodesCount = 200;
        int clustersCount = 5;
        double pIn = 0.30;
        double pOut = 0.04;
        int pointsCount = 100;

        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        GraphBundle graphs;

        properties.setP_out(0.03);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("MinSpanningTree: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.03")
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.04);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("MinSpanningTree: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.04")
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.05);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("MinSpanningTree: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.05")
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.1);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("MinSpanningTree: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.1")
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.03);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("Ward: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.03")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.04);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("Ward: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.04")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.05);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("Ward: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.05")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        properties.setP_out(0.1);
        graphs = ClusteredGraphGenerator.getInstance().generate(properties);
        new TaskChain("Ward: 200 nodes, 5 clusters, 10 graphs, pIn=0.3, pOut=0.1")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 5), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();
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
                .execute()
                .draw();
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
        int pointsCount = 150;

        Graph graph = new CSVReader().importGraph("data/polbooks_nodes.csv", "data/polbooks_edges.csv");
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(1, 105, 3, 0, 0);
        GraphBundle graphs = new GraphBundle(new ArrayList<Graph>() {{
            add(graph);
        }}, properties);

        new TaskChain("Political books: 105 nodes, 3 clusters, Ward")
                .addTasks(ScenarioHelper.defaultTasks(new WardChecker(graphs, 3), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        new TaskChain("Political books: 105 nodes, 3 clusters, MinSpanningTree")
                .addTasks(ScenarioHelper.defaultTasks(new MinSpanningTreeChecker(graphs, 3), Metric.getDefaultDistances(), pointsCount))
                .execute().draw();

        GraphMLWriter writer = new GraphMLWriter();
        writer.writeGraph(graphs.getGraphs().get(0), ContextProvider.getInstance().getContext().getImgFolder() + "/polbooks.graphml");
    }
}

