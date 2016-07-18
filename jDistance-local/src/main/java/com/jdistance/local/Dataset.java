package com.jdistance.local;

import com.jdistance.graph.GraphBundle;
import com.jdistance.local.adapter.graph.CSVGraphBuilder;

import java.io.IOException;

public enum Dataset {
    FOOTBALL("football", "dataset/football_nodes.csv", "dataset/football_edges.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getPolbooksOrFootball(name, pathToNodes, pathToEdges);
        }
    },
    POLBOOKS("polbooks", "dataset/polbooks_nodes.csv", "dataset/polbooks_edges.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getPolbooksOrFootball(name, pathToNodes, pathToEdges);
        }
    },
    POLBLOGS("polblogs", "dataset/polblogs.net", null) {
        @Override
        public GraphBundle get() throws IOException {
            return getPolblogsOrZachary(name, pathToNodes);
        }
    },
    ZACHARY("zachary", "dataset/zachary.net", null) {
        @Override
        public GraphBundle get() throws IOException {
            return getPolblogsOrZachary(name, pathToNodes);
        }
    },
    news_2cl_1("news_2cl_1", "dataset/newsgroup/news_2cl_1_classeo.csv", "dataset/newsgroup/news_2cl_1_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_2cl_2("news_2cl_2", "dataset/newsgroup/news_2cl_2_classeo.csv", "dataset/newsgroup/news_2cl_2_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_2cl_3("news_2cl_3", "dataset/newsgroup/news_2cl_3_classeo.csv", "dataset/newsgroup/news_2cl_3_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_1("news_3cl_1", "dataset/newsgroup/news_3cl_1_classeo.csv", "dataset/newsgroup/news_3cl_1_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_2("news_3cl_2", "dataset/newsgroup/news_3cl_2_classeo.csv", "dataset/newsgroup/news_3cl_2_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_3cl_3("news_3cl_3", "dataset/newsgroup/news_3cl_3_classeo.csv", "dataset/newsgroup/news_3cl_3_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_1("news_5cl_1", "dataset/newsgroup/news_5cl_1_classeo.csv", "dataset/newsgroup/news_5cl_1_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_2("news_5cl_2", "dataset/newsgroup/news_5cl_2_classeo.csv", "dataset/newsgroup/news_5cl_2_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
            return getNewsgroupGraph(name, pathToNodes, pathToEdges);
        }
    },
    news_5cl_3("news_5cl_3", "dataset/newsgroup/news_5cl_3_classeo.csv", "dataset/newsgroup/news_5cl_3_Docr.csv") {
        @Override
        public GraphBundle get() throws IOException {
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

    public abstract GraphBundle get() throws IOException;

    private static GraphBundle getPolbooksOrFootball(String name, String pathToNodes, String pathToEdges) throws IOException {
        return new CSVGraphBuilder()
                .setName(name)
                .importNodesIdNameClass(pathToNodes)
                .importEdgesList(pathToEdges)
                .shuffleAndBuildBundle();
    }

    private static GraphBundle getPolblogsOrZachary(String name, String pathToGraph) throws IOException {
        return new CSVGraphBuilder()
                .setName(name)
                .importNodesAndEdges(pathToGraph)
                .shuffleAndBuildBundle();
    }

    private static GraphBundle getNewsgroupGraph(String name, String pathToNodes, String pathToEdges) throws IOException {
        return new CSVGraphBuilder()
                .setName(name)
                .importNodesClassOnly(pathToNodes)
                .importAdjacencyMatrix(pathToEdges)
                .shuffleAndBuildBundle();
    }
}
