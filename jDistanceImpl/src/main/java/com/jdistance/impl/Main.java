package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.impl.adapter.graph.GraphMLWriter;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Kernel;
import com.jdistance.metric.Metric;
import jeigen.DenseMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException {
        testFor(2, 100, "[0.49:1]", "0.1");
        testFor(2, 200, "[0.49:1]", "0.1");
        testFor(2, 300, "[0.49:1]", "0.1");
        testFor(2, 400, "[0.49:1]", "0.1");
    }

    private static void allNewsGroups() throws ParserConfigurationException, SAXException, IOException {
        List<String[]> newsgroups = Arrays.asList(
//                new String[] {"news_2cl_1", "data/newsgroup/news_2cl_1_classeo.csv", "data/newsgroup/news_2cl_1_Docr.csv", "[0.49:1.0]", "0.1"},
//                new String[] {"news_2cl_2", "data/newsgroup/news_2cl_2_classeo.csv", "data/newsgroup/news_2cl_2_Docr.csv", "[0.49:1.0]", "0.1"},
                new String[]{"news_2cl_3", "data/newsgroup/news_2cl_3_classeo.csv", "data/newsgroup/news_2cl_3_Docr.csv", "[0.49:1.0]", "0.1"},
//                new String[] {"news_3cl_1", "data/newsgroup/news_3cl_1_classeo.csv", "data/newsgroup/news_3cl_1_Docr.csv", "[0.33:1.0]", "0.1"}
                new String[]{"news_3cl_2", "data/newsgroup/news_3cl_2_classeo.csv", "data/newsgroup/news_3cl_2_Docr.csv", "[0.33:1.0]", "0.1"}
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

    public static void testFor(int clustersCount, int nodesCount, String yrange, String yticks) {
        GraphBundle graphs = GnPInPOutGraphGenerator.getInstance().generate(new GeneratorPropertiesDTO(2, nodesCount, clustersCount, 0.3, 0.18));

        TaskChainBuilder kernelBuilder = new TaskChainBuilder(Metric.getDefaultDistances(), 22);
        kernelBuilder.setGraphs(graphs).generateWardTasks().setName("Kernel2without " + kernelBuilder.getName()).build()
                .execute()
                .drawUniqueAndBezier(yrange, yticks)
                .writeData();

//        TaskChainBuilder metricBuilder = new TaskChainBuilder(Metric.getDefaultDistances(), 101);
//        metricBuilder.setGraphs(graphs).generateWardTasks().setName("Metric " + metricBuilder.getName()).build()
//                .execute()
//                .drawUniqueAndBezier(yrange, yticks)
//                .writeData();
    }

    public static void testKernels() throws IOException, SAXException, TransformerConfigurationException {
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly("data/newsgroup/news_2cl_1_classeo.csv")
                .importAdjacencyMatrix("data/newsgroup/news_2cl_1_Docr.csv")
                .shuffleAndBuildBundle();
        new GraphMLWriter().writeGraph(graphs.getGraphs().get(0), "results/out.graphml");
    }

    public static void findCorellation() throws IOException {
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly("data/newsgroup/news_2cl_1_classeo.csv")
                .importAdjacencyMatrix("data/newsgroup/news_2cl_1_Docr.csv")
                .shuffleAndBuildBundle();
        DenseMatrix A = graphs.getGraphs().get(0).getA();
        DenseMatrix forest = Metric.FOREST.getD(A, 10000);
        DenseMatrix spct = Metric.SP_CT.getD(A, 1.0);
        double corr = new PearsonsCorrelation().correlation(forest.getValues(), spct.getValues());
        System.out.println(corr);
    }
}

