package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Datasets {
    public static final DatasetPOJO football = new DatasetPOJO("football", "data/football_nodes.csv", "data/football_edges.csv");
    public static final DatasetPOJO polbooks = new DatasetPOJO("polbooks", "data/polbooks_nodes.csv", "data/polbooks_edges.csv");
    public static final List<DatasetPOJO> newsgroups = Arrays.asList(
            new DatasetPOJO("news_2cl_1", "data/newsgroup/news_2cl_1_classeo.csv", "data/newsgroup/news_2cl_1_Docr.csv"),
            new DatasetPOJO("news_2cl_2", "data/newsgroup/news_2cl_2_classeo.csv", "data/newsgroup/news_2cl_2_Docr.csv"),
            new DatasetPOJO("news_2cl_3", "data/newsgroup/news_2cl_3_classeo.csv", "data/newsgroup/news_2cl_3_Docr.csv"),
            new DatasetPOJO("news_3cl_1", "data/newsgroup/news_3cl_1_classeo.csv", "data/newsgroup/news_3cl_1_Docr.csv"),
            new DatasetPOJO("news_3cl_2", "data/newsgroup/news_3cl_2_classeo.csv", "data/newsgroup/news_3cl_2_Docr.csv"),
            new DatasetPOJO("news_3cl_3", "data/newsgroup/news_3cl_3_classeo.csv", "data/newsgroup/news_3cl_3_Docr.csv"),
            new DatasetPOJO("news_5cl_1", "data/newsgroup/news_5cl_1_classeo.csv", "data/newsgroup/news_5cl_1_Docr.csv"),
            new DatasetPOJO("news_5cl_2", "data/newsgroup/news_5cl_2_classeo.csv", "data/newsgroup/news_5cl_2_Docr.csv"),
            new DatasetPOJO("news_5cl_3", "data/newsgroup/news_5cl_3_classeo.csv", "data/newsgroup/news_5cl_3_Docr.csv")
    );

    private static GraphBundle getPolbooksOrFootball(DatasetPOJO dataset) throws IOException {
        return new CSVGraphBuilder()
                .setName(dataset.getName())
                .importNodesIdNameClass(dataset.getPathToNodes())
                .importEdgesList(dataset.getPathToEdges())
                .shuffleAndBuildBundle();
    }

    private static GraphBundle getNewsgroupGraph(DatasetPOJO dataset) throws IOException {
        return new CSVGraphBuilder()
                .setName(dataset.getName())
                .importNodesClassOnly(dataset.getPathToNodes())
                .importAdjacencyMatrix(dataset.getPathToEdges())
                .shuffleAndBuildBundle();
    }

    private static class DatasetPOJO {
        private String name;
        private String pathToNodes;
        private String pathToEdges;

        DatasetPOJO(String name, String pathToNodes, String pathToEdges) {
            this.name = name;
            this.pathToNodes = pathToNodes;
            this.pathToEdges = pathToEdges;
        }

        String getName() {
            return name;
        }

        String getPathToNodes() {
            return pathToNodes;
        }

        String getPathToEdges() {
            return pathToEdges;
        }
    }
}
