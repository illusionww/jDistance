package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.impl.adapter.graph.DCRGraphMLReader;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Kernel;
import com.jdistance.metric.Metric;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
        testKernels();
    }

    private static void allNewsGroups() throws ParserConfigurationException, SAXException, IOException {
        List<String[]> newsgroups = Arrays.asList(
//                new String[] {"news_2cl_1", "data/newsgroup/news_2cl_1_classeo.csv", "data/newsgroup/news_2cl_1_Docr.csv", "[0.49:1.0]", "0.1"},
//                new String[] {"news_2cl_2", "data/newsgroup/news_2cl_2_classeo.csv", "data/newsgroup/news_2cl_2_Docr.csv", "[0.49:1.0]", "0.1"},
                new String[] {"news_2cl_3", "data/newsgroup/news_2cl_3_classeo.csv", "data/newsgroup/news_2cl_3_Docr.csv", "[0.49:1.0]", "0.1"},
//                new String[] {"news_3cl_1", "data/newsgroup/news_3cl_1_classeo.csv", "data/newsgroup/news_3cl_1_Docr.csv", "[0.33:1.0]", "0.1"}
                new String[] {"news_3cl_2", "data/newsgroup/news_3cl_2_classeo.csv", "data/newsgroup/news_3cl_2_Docr.csv", "[0.33:1.0]", "0.1"}
//                new String[] {"news_3cl_3", "data/newsgroup/news_3cl_3_classeo.csv", "data/newsgroup/news_3cl_3_Docr.csv", "[0.33:1.0]", "0.1"},
//                new String[] {"news_5cl_1", "data/newsgroup/news_5cl_1_classeo.csv", "data/newsgroup/news_5cl_1_Docr.csv", "[0.2:1.0]", "0.2"},
//                new String[] {"news_5cl_2", "data/newsgroup/news_5cl_2_classeo.csv", "data/newsgroup/news_5cl_2_Docr.csv", "[0.2:1.0]", "0.2"},
//                new String[] {"news_5cl_3", "data/newsgroup/news_5cl_3_classeo.csv", "data/newsgroup/news_5cl_3_Docr.csv", "[0.2:1.0]", "0.2"}
        );
        for (String[] array : newsgroups) {
            CSVGraphsNewsgroup(array[0], array[1], array[2], array[3], array[4]);
        }
    }

    private static void CSVGraphsNewsgroup(String name, String pathToClasses, String pathToA, String yrange, String yticks) throws IOException {
        int pointsCount = 101;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly(pathToClasses)
                .importAdjacencyMatrix(pathToA)
                .shuffleAndBuildBundle();
        new TaskChainBuilder(name + ", Diffusion", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier(yrange, yticks)
                .writeStatistics();
        new TaskChainBuilder(name + ", Ward", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier(yrange, yticks)
                .writeStatistics();
//          new TaskChainBuilder(name + ", Statistics", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateStubTasks().build().execute().writeStatistics();

    }

    private static void football() throws IOException {
        GraphBundle football = new CSVGraphBuilder()
                .importNodesIdNameClass("data/football_nodes.csv")
                .importEdgesList("data/football_edges.csv")
                .shuffleAndBuildBundle();
//        new TaskChainBuilder("football_ward", Metric.getDefaultDistances(), 201)
//                .setGraphs(football).generateWardTasks().build().execute().writeData().drawUniqueAndBezier("[0.8:1.0]", "0.05");
        new TaskChainBuilder("football_sta", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateStubTasks().build().execute().writeStatistics();

    }

    private static void politicalBooks() throws IOException {
        GraphBundle football = new CSVGraphBuilder()
                .importNodesIdNameClass("data/polbooks_nodes.csv")
                .importEdgesList("data/polbooks_edges.csv")
                .shuffleAndBuildBundle();
        new TaskChainBuilder("polbooks_diffusion", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateDiffusionTasks().build().execute().writeData().drawUnique("[0.75:1.0]", "0.05");
        new TaskChainBuilder("polbooks_ward", Metric.getDefaultDistances(), 201)
                .setGraphs(football).generateWardTasks().build().execute().writeData().drawUniqueAndBezier("[0.45:1.0]", "0.1");
    }

    private static void fixGraphs() {
        fixGraph("polbooks_ward", "results/data/polbooks_ward.csv", "[0.39:1]", "0.2");
    }

    private static void fixGraph(String name, String path, String yrange, String yticks) {
        new TaskChainBuilder(name, null, null).importDataFromFile(path).build()
                .drawUniqueAndBezier(yrange, yticks);
    }

    public static void testHeatKernel() {
        GraphBundle graphs = GnPInPOutGraphGenerator.getInstance().generate(new GeneratorPropertiesDTO(3, 100, 2, 0.3, 0.1));
        new TaskChainBuilder("Kernel", Kernel.getDefaultKernels(), 101)
                .setGraphs(graphs)
                .generateWardTasks().build().execute()
                .drawUniqueAndBezier("[0.3:1]", "0.2");
        new TaskChainBuilder("Metric", Metric.getDefaultDistances(), 101)
                .setGraphs(graphs)
                .generateWardTasks().build().execute()
                .drawUniqueAndBezier("[0.3:1]", "0.2");
        new TaskChainBuilder("Diffusion", Metric.getDefaultDistances(), 101)
                .setGraphs(graphs)
                .generateDiffusionTasks().build().execute()
                .drawUniqueAndBezier("[0.3:1]", "0.2");

//        new TaskChainBuilder("150 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 150, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
//        new TaskChainBuilder("200 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 200, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
//        new TaskChainBuilder("250 Heat 31", Metric.getDefaultDistances(), 101)
//                .generateGraphs(3, 250, 4, 0.3, 0.1).generateWardTasks().build().execute().drawUniqueAndBezier("[0.3:1]", "0.2");
    }

    public static void testKernels() throws IOException, SAXException, TransformerConfigurationException {
            GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly("data/newsgroup/news_2cl_1_classeo.csv")
                .importAdjacencyMatrix("data/newsgroup/news_2cl_1_Docr.csv")
                .shuffleAndBuildBundle();
        new GraphMLWriter().writeGraph(graphs.getGraphs().get(0), "results/out.graphml");
    }
}

