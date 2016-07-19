package com.jdistance.spark;

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

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        task();

        sparkContext.stop();
    }

    public static void task() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, new int[]{
                65, 35, 25, 13, 8, 4 // sum = 150
        }, new double[][]{
                {0.30, 0.20, 0.10, 0.15, 0.07, 0.25, 0.14},
                {0.00, 0.24, 0.08, 0.13, 0.05, 0.17, 0.19},
                {0.00, 0.00, 0.16, 0.09, 0.04, 0.12, 0.10},
                {0.00, 0.00, 0.00, 0.20, 0.02, 0.14, 0.15},
                {0.00, 0.00, 0.00, 0.00, 0.12, 0.04, 0.08},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.40, 0.14},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.25}
        }));

        new GridSearch().addLinesForDifferentMeasures(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Kernel.getAllK(), graphs, 55)
                .execute()
                .writeData("7 clusters");
    }
}
