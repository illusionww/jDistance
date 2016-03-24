package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Metric;
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
                new String[] {"news_3cl_1", "data/newsgroup/news_3cl_1_classeo.csv", "data/newsgroup/news_3cl_1_Docr.csv", "[0.33:1.0]"},
                new String[] {"news_3cl_2", "data/newsgroup/news_3cl_2_classeo.csv", "data/newsgroup/news_3cl_2_Docr.csv", "[0.33:1.0]"},
                new String[] {"news_3cl_3", "data/newsgroup/news_3cl_3_classeo.csv", "data/newsgroup/news_3cl_3_Docr.csv", "[0.33:1.0]"},
                new String[] {"news_5cl_1", "data/newsgroup/news_5cl_1_classeo.csv", "data/newsgroup/news_5cl_1_Docr.csv", "[0.2:1.0]"},
                new String[] {"news_5cl_2", "data/newsgroup/news_5cl_2_classeo.csv", "data/newsgroup/news_5cl_2_Docr.csv", "[0.2:1.0]"},
                new String[] {"news_5cl_3", "data/newsgroup/news_5cl_3_classeo.csv", "data/newsgroup/news_5cl_3_Docr.csv", "[0.2:1.0]"}
        );
        for (String[] array : newsgroups) {
            CSVGraphsNewsgroup(array[0], array[1], array[2], array[3]);
        }
    }

    private static void CSVGraphsNewsgroup(String name, String pathToClasses, String pathToA, String yrange) throws IOException, ParserConfigurationException, SAXException {
        int pointsCount = 101;
        GraphBundle graphs = new CSVGraphBuilder()
                .importNodesClassOnly(pathToClasses)
                .importAdjacencyMatrix(pathToA)
                .buildBundle();
//        new TaskChainBuilder(name + ", MinSpanningTree", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateMinSpanningTreeTasks().build().execute().write().draw(yrange);
//        new TaskChainBuilder(name + ", Diffusion", Metric.getDefaultDistances(), pointsCount)
//                .setGraphs(graphs).generateDiffusionTasks().build().execute().write().draw(yrange);
        new TaskChainBuilder(name + ", Ward", Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute().write().draw(yrange);
    }
}

