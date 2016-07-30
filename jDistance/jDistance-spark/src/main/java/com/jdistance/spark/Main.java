package com.jdistance.spark;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.KernelWrapper;
import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.GridSearch;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        task();

        sparkContext.stop();
    }

    public static void task() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(20, 200, 2, 0.3, 0.1));
        new GridSearch().addLinesForDifferentMeasures(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Arrays.asList(
                new KernelWrapper(Kernel.COMM_H),
                new KernelWrapper(Kernel.LOG_COMM_H),
                new KernelWrapper(Kernel.DUMMY_COMM_H),
                new KernelWrapper(Kernel.DUMMY_LOG_COMM_H)
        ), graphs, 51)
                .execute()
                .writeData("");
    }
}
