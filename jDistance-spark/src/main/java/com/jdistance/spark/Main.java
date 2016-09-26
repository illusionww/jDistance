package com.jdistance.spark;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Axis;
import com.jdistance.learning.Collapse;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.spark.workflow.BroadcastedGraphBundle;
import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.GridSearch;
import com.jdistance.workflow.CartesianTaskListBuilder;
import com.jdistance.workflow.Task;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        task();

        sparkContext.stop();
    }

    public static void task() {
        List<GraphBundle> graphBundles = new ArrayList<>();
        List<Integer> firstList = Arrays.asList(1, 2, 5, 7, 10, 15, 20, 25, 30, 35, 40, 45, 50);
        for (int first : firstList) {
            int second = 100 - first;
            GraphBundle oldGraphBundle = new GnPInPOutGraphGenerator().generate(Double.toString(first), new GeneratorPropertiesPOJO(350, new int[]{
                    first, second
            }, new double[][]{
                    {0.3, 0.1},
                    {0.1, 0.3}
            }));
            graphBundles.add(new BroadcastedGraphBundle(oldGraphBundle));
        }
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(graphBundles)
                .setMeasures(Kernel.getAllH_plusRSP_FE())
                .linspaceMeasureParams(55)
                .build();
        new GridSearch(tasks)
                .execute()
                .writeData(Axis.GRAPHS, Axis.MEASURE, Collapse.MAX);
    }
}
