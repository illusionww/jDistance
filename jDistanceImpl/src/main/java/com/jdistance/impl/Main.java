package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        allNewsGroups();
    }

    private static void allNewsGroups() throws ParserConfigurationException, SAXException, IOException {
        List<String[]> newsgroups = Arrays.asList(
                new String[] {"news_2cl_1", "data/newsgroup/news_2cl_1_classeo.csv", "data/newsgroup/news_2cl_1_Docr.csv", "[0.49:1.0]", "0.1"},
                new String[] {"news_2cl_2", "data/newsgroup/news_2cl_2_classeo.csv", "data/newsgroup/news_2cl_2_Docr.csv", "[0.49:1.0]", "0.1"},
                new String[] {"news_2cl_3", "data/newsgroup/news_2cl_3_classeo.csv", "data/newsgroup/news_2cl_3_Docr.csv", "[0.49:1.0]", "0.1"},
                new String[] {"news_3cl_1", "data/newsgroup/news_3cl_1_classeo.csv", "data/newsgroup/news_3cl_1_Docr.csv", "[0.33:1.0]", "0.1"},
                new String[] {"news_3cl_2", "data/newsgroup/news_3cl_2_classeo.csv", "data/newsgroup/news_3cl_2_Docr.csv", "[0.33:1.0]", "0.1"},
                new String[] {"news_3cl_3", "data/newsgroup/news_3cl_3_classeo.csv", "data/newsgroup/news_3cl_3_Docr.csv", "[0.33:1.0]", "0.1"},
                new String[] {"news_5cl_1", "data/newsgroup/news_5cl_1_classeo.csv", "data/newsgroup/news_5cl_1_Docr.csv", "[0.2:1.0]", "0.2"},
                new String[] {"news_5cl_2", "data/newsgroup/news_5cl_2_classeo.csv", "data/newsgroup/news_5cl_2_Docr.csv", "[0.2:1.0]", "0.2"},
                new String[] {"news_5cl_3", "data/newsgroup/news_5cl_3_classeo.csv", "data/newsgroup/news_5cl_3_Docr.csv", "[0.2:1.0]", "0.2"}
        );
        for (String[] array : newsgroups) {
            CSVGraphsNewsgroup(array[0], array[1], array[2], array[3], array[4]);
        }
    }

    private static void CSVGraphsNewsgroup(String name, String pathToClasses, String pathToA, String yrange, String yticks) throws IOException {
        int pointsCount = 201;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly(pathToClasses)
                .importAdjacencyMatrix(pathToA)
                .shuffleAndBuildBundle();
//        new TaskChainBuilder(name + ", MinSpanningTree", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateMinSpanningTreeTasks().build().execute().writeData().drawUniqueAndBezier(yrange, yticks);
//        new TaskChainBuilder(name + ", Diffusion", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateDiffusionTasks().build().execute().writeData().drawUniqueAndBezier(yrange, yticks).writeStatistics();
//        new TaskChainBuilder(name + ", Ward", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateWardTasks().build().execute().writeData().drawUniqueAndBezier(yrange, yticks).writeStatistics();
          new TaskChainBuilder(name + ", Statistics", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute().writeStatistics();

    }

    private static void football() throws IOException {
        GraphBundle football = new CSVGraphBuilder()
                .importNodesIdNameClass("data/football_nodes.csv")
                .importEdgesList("data/football_edges.csv")
                .shuffleAndBuildBundle();
        new TaskChainBuilder("football_minspanningtree", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateMinSpanningTreeTasks().build().execute().writeData().drawUniqueAndBezier("[0.2:1.0]", "0.2");
        new TaskChainBuilder("football_diffusion", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateDiffusionTasks().build().execute().writeData().drawUnique("[0.97:1.0]", "0.01");
        new TaskChainBuilder("football_ward", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateWardTasks().build().execute().writeData().drawUniqueAndBezier("[0.8:1.0]", "0.05");
    }

    private static void politicalBooks() throws IOException {
        GraphBundle football = new CSVGraphBuilder()
                .importNodesIdNameClass("data/polbooks_nodes.csv")
                .importEdgesList("data/polbooks_edges.csv")
                .shuffleAndBuildBundle();
        new TaskChainBuilder("polbooks_minspanningtree", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateMinSpanningTreeTasks().build().execute().writeData().drawUniqueAndBezier("[0.2:1.0]", "0.2");
        new TaskChainBuilder("polbooks_diffusion", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateDiffusionTasks().build().execute().writeData().drawUnique("[0.75:1.0]", "0.05");
        new TaskChainBuilder("polbooks_ward", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateWardTasks().build().execute().writeData().drawUniqueAndBezier("[0.45:1.0]", "0.1");
    }

    private static void fixGraphs() {
        fixGraph("polbooks_minspanningtree", "results/data/polbooks_minspanningtree.csv", "[0.39:1]", "0.2");
        fixGraph("polbooks_ward", "results/data/polbooks_ward.csv", "[0.39:1]", "0.2");
    }

    private static void fixGraph(String name, String path, String yrange, String yticks) {
        new TaskChainBuilder(name, null, null).importDataFromFile(path).build()
                .drawUniqueAndBezier(yrange, yticks);
    }

    public static void testHeatKernel() {
        new TaskChainBuilder("100 Heat", Arrays.asList(
                new MetricWrapper("Heat Fair", Metric.HEAT_FAIR),
                new MetricWrapper("logHeat Fair", Metric.LOG_HEAT_FAIR),
                new MetricWrapper("Heat31", Metric.HEAT31),
                new MetricWrapper("logHeat31", Metric.LOG_HEAT31),
                new MetricWrapper("Heat30", Metric.LOG_HEAT30),
                new MetricWrapper("logHeat31", Metric.LOG_HEAT31)
        ), 301)
                .generateGraphs(1, 100, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2").writeStatistics();
//        new TaskChainBuilder("150 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 150, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
//        new TaskChainBuilder("200 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 200, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
//        new TaskChainBuilder("250 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 250, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
    }
}

