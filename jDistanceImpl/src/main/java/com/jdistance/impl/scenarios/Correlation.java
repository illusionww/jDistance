package com.jdistance.impl.scenarios;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.adapter.graph.CSVGraphBuilder;
import com.jdistance.metric.Metric;
import jeigen.DenseMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import java.io.IOException;

public class Correlation {
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
