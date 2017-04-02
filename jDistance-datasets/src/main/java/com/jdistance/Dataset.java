package com.jdistance;

import com.jdistance.graph.GraphBundle;

import java.io.IOException;

public enum Dataset {
    FOOTBALL("football", "datasets/football_nodes.csv", "datasets/football_edges.csv") {
        @Override
        public GraphBundle get() {
            return getPolbooksOrFootball(name, pathToNodes, pathToEdges);
        }
    },
    POLBOOKS("polbooks", "datasets/polbooks_nodes.csv", "datasets/polbooks_edges.csv") {
        @Override
        public GraphBundle get() {
            return getPolbooksOrFootball(name, pathToNodes, pathToEdges);
        }
    },
    POLBLOGS("polblogs", "datasets/polblogs.net", null) {
        @Override
        public GraphBundle get() {
            return getPolblogsOrZachary(name, pathToNodes);
        }
    },
    ZACHARY("zachary", "datasets/zachary.net", null) {
        @Override
        public GraphBundle get() {
            return getPolblogsOrZachary(name, pathToNodes);
        }
    },
    news_2cl_1("news_2cl_1", "datasets/newsgroup/news_2cl_1_classeo.csv", "datasets/newsgroup/news_2cl_1_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_2cl_2("news_2cl_2", "datasets/newsgroup/news_2cl_2_classeo.csv", "datasets/newsgroup/news_2cl_2_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_2cl_3("news_2cl_3", "datasets/newsgroup/news_2cl_3_classeo.csv", "datasets/newsgroup/news_2cl_3_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_1("news_3cl_1", "datasets/newsgroup/news_3cl_1_classeo.csv", "datasets/newsgroup/news_3cl_1_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_2("news_3cl_2", "datasets/newsgroup/news_3cl_2_classeo.csv", "datasets/newsgroup/news_3cl_2_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_3("news_3cl_3", "datasets/newsgroup/news_3cl_3_classeo.csv", "datasets/newsgroup/news_3cl_3_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_1("news_5cl_1", "datasets/newsgroup/news_5cl_1_classeo.csv", "datasets/newsgroup/news_5cl_1_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_2("news_5cl_2", "datasets/newsgroup/news_5cl_2_classeo.csv", "datasets/newsgroup/news_5cl_2_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_3("news_5cl_3", "datasets/newsgroup/news_5cl_3_classeo.csv", "datasets/newsgroup/news_5cl_3_Docr.csv") {
        @Override
        public GraphBundle get() {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    };

    protected String name;
    protected String pathToNodes;
    protected String pathToEdges;

    Dataset(String name, String pathToNodes, String pathToEdges) {
        this.name = name;
        this.pathToNodes = pathToNodes;
        this.pathToEdges = pathToEdges;
    }

    public abstract GraphBundle get();

    private static GraphBundle getPolbooksOrFootball(String name, String pathToNodes, String pathToEdges) {
        try {
            return new CSVGraphBuilder()
                    .setName(name)
                    .importNodesIdNameClass(pathToNodes)
                    .importEdgesList(pathToEdges)
                    .buildBundle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphBundle getPolblogsOrZachary(String name, String pathToGraph) {
        try {
            return new CSVGraphBuilder()
                    .setName(name)
                    .importNodesAndEdges(pathToGraph)
                    .buildBundle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static GraphBundle getNewsgroupGraph(String name, String pathToNodes, String pathToEdges) {
        try {
            return new CSVGraphBuilder()
                    .setName(name)
                    .importNodesClassOnly(pathToNodes)
                    .importAdjacencyMatrix(pathToEdges)
                    .buildBundle();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
