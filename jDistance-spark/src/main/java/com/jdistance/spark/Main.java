package com.jdistance.spark;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
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

        old_task();

        sparkContext.stop();
    }

    public static void task() {
        List<Dataset> datasets = Arrays.asList(
                Dataset.FOOTBALL,
                Dataset.POLBOOKS,
                Dataset.ZACHARY,
                Dataset.news_2cl_1,
                Dataset.news_2cl_2,
                Dataset.news_2cl_3,
                Dataset.news_3cl_1,
                Dataset.news_3cl_2,
                Dataset.news_3cl_3,
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
                            50)
                    .execute()
                    .writeData();
        }
    }

    public static void old_task() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(25, 150, 3, 0.3, 0.1));
        new GridSearch().addLinesForDifferentMeasures(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Kernel.getAllK(), graphs, 55)
                .execute()
                .writeData("7 clusters");
    }
}
