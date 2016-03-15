package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Metric;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        CSVGraphsNewsgroup();
    }

    private static void CSVGraphsPoliticalBooks() throws IOException, SAXException, ParserConfigurationException {
        int pointsCount = 200;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesIdNameClass("data/polbooks_nodes.csv")
                .importEdgesList("data/polbooks_edges.csv")
                .buildBundle();
        new TaskChainBuilder("Polbooks: 105 nodes, 3 clusters, MinSpanningTree", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateMinSpanningTreeTasks().build().execute().write().draw();
        new TaskChainBuilder("Polbooks: 105 nodes, 3 clusters, Diffusion", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute().write().draw();
        new TaskChainBuilder("Polbooks: 105 nodes, 3 clusters, Ward", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute().write().draw();
    }

    private static void CSVGraphsFootball() throws IOException, SAXException, ParserConfigurationException {
        int pointsCount = 200;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesIdNameClass("data/football_nodes.csv")
                .importEdgesList("data/football_edges.csv")
                .buildBundle();
        new TaskChainBuilder("Football: 115 nodes, 12 clusters, MinSpanningTree", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateMinSpanningTreeTasks().build().execute().write().draw();
        new TaskChainBuilder("Football: 115 nodes, 12 clusters, Diffusion", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute().write().draw();
        new TaskChainBuilder("Football: 115 nodes, 12 clusters, Ward", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute().write().draw();
//        GraphMLWriter writer = new GraphMLWriter();
//        writer.writeGraph(graphs.getGraphs().get(0), ContextProvider.getInstance().getContext().getImgFolder() + "/football.graphml");
    }

    private static void CSVGraphsNewsgroup() throws IOException, ParserConfigurationException, SAXException {
        int pointsCount = 50;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly("data/newsgroup/news_2cl_1_classeo.csv")
                .importAdjacencyMatrix("data/newsgroup/news_2cl_1_Docr.csv")
                .buildBundle();
//        new TaskChainBuilder("news_2cl_1, MinSpanningTree", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateMinSpanningTreeTasks().build().execute().write().draw();
        new TaskChainBuilder("news_2cl_1, Diffusion", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute().write().draw();
//        new TaskChainBuilder("news_2cl_1, Ward", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateWardTasks().build().execute().write().draw();
    }

}

