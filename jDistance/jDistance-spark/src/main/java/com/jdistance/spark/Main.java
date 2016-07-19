package com.jdistance.spark;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.TaskPool;
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
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, new int[]{
                100, 75, 75, 50, 25, 10, 5 // sum = 340
        }, new double[][]{
                {0.30, 0.15, 0.10, 0.13, 0.02, 0.20, 0.10},
                {0.00, 0.20, 0.10, 0.13, 0.02, 0.20, 0.10},
                {0.00, 0.00, 0.16, 0.13, 0.02, 0.20, 0.10},
                {0.00, 0.00, 0.00, 0.13, 0.02, 0.20, 0.10},
                {0.00, 0.00, 0.00, 0.00, 0.10, 0.20, 0.10},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.30, 0.10},
                {0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.14}
        }));
        new TaskPool().addLinesForDifferentMeasures(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Kernel.getAllK(), graphs, 55)
                .execute()
                .writeData("7 clusters");
    }
}
