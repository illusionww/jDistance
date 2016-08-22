package com.jdistance.spark;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.GridSearch;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        task();

        sparkContext.stop();
    }

    public static void task() {
        List<Dataset> datasets = Arrays.asList(
                Dataset.news_5cl_1,
                Dataset.news_5cl_2,
                Dataset.news_5cl_3
        );

        for (Dataset dataset : datasets) {
            GraphBundle graphs = dataset.get();
            new GridSearch(dataset.name())
                    .addLinesForDifferentMeasures(
                            new Ward(graphs.getProperties().getClustersCount()),
                            Scorer.ARI,
                            Kernel.getAllH_plusRSP_FE(),
                            graphs,
                            55)
                    .execute()
                    .writeData();
        }
    }
}
