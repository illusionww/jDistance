package com.jdistance.spark;

import com.jdistance.Dataset;
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

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .set("spark.shuffle.blockTransferService", "nio")
                .setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        sixClusters();

        sparkContext.stop();
    }

    private static void twoClusters() {
        List<GraphBundle> graphBundles = new ArrayList<>();
        List<Integer> firstList = Arrays.asList(1, 2, 5, 7, 9, 12, 15, 20, 25, 30, 35, 40, 45, 50);
        for (int first : firstList) {
            int second = 100 - first;
            GraphBundle oldGraphBundle = new GnPInPOutGraphGenerator().generate(Double.toString(first), new GeneratorPropertiesPOJO(300, new int[]{
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
                .writeData(Axis.GRAPHS, Axis.MEASURE, Collapse.AVERAGE);
    }

    public static void datasets() {
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
            GraphBundle graphBundle = dataset.get();
            List<Task> tasks = new CartesianTaskListBuilder()
                    .setEstimators(Estimator.WARD)
                    .setScorers(Scorer.ARI)
                    .setGraphBundles(graphBundle)
                    .setMeasures(Kernel.getAllH_plusRSP_FE())
                    .linspaceMeasureParams(55)
                    .build();
            new GridSearch(graphBundle.getName(), tasks)
                    .execute()
                    .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
        }
    }

    private static void competitions() {
        Map<String, List<GraphBundle>> graphBundles = new HashMap<>();
//        graphBundles.put("n100c4pout015",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 4, 0.3, 0.15)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
        graphBundles.put("n100c2pout015",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.15)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n100c2pout01",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.1)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n100c4pout01",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 4, 0.3, 0.1)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n200c4pout015",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 4, 0.3, 0.15)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n200c2pout01",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 2, 0.3, 0.1)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n200c2pout015",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 2, 0.3, 0.15)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );
        graphBundles.put("n200c4pout01",
                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 4, 0.3, 0.1)).getGraphs().stream()
                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
                        .collect(Collectors.toList())
        );

        for (Map.Entry<String, List<GraphBundle>> graphBundle : graphBundles.entrySet()) {
            List<Task> tasks = new CartesianTaskListBuilder()
                    .setEstimators(Estimator.WARD)
                    .setScorers(Scorer.ARI)
                    .setGraphBundles(graphBundle.getValue())
                    .setMeasures(Kernel.getAllH_plusRSP_FE())
                    .linspaceMeasureParams(55)
                    .build();
            new GridSearch(graphBundle.getKey(), tasks)
                    .execute()
                    .writeData(Axis.MEASURE_PARAM, Axis.GRAPHSnMEASURE, Collapse.CHECK_ONLY_ONE);
        }
    }

    private static void sixClusters() {
        GraphBundle graphBundle = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(300, new int[]{
                65, 35, 25, 13, 8, 4 // sum = 150
        }, new double[][]{
                {0.30, 0.20, 0.10, 0.13, 0.02, 0.20},
                {0.20, 0.30, 0.10, 0.13, 0.02, 0.20},
                {0.10, 0.10, 0.16, 0.13, 0.02, 0.20},
                {0.13, 0.13, 0.13, 0.25, 0.02, 0.20},
                {0.02, 0.02, 0.02, 0.02, 0.10, 0.20},
                {0.20, 0.20, 0.20, 0.20, 0.20, 0.30}
        }));
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(graphBundle)
                .setMeasures(Kernel.getAllH_plusRSP_FE())
                .linspaceMeasureParams(55)
                .build();
        new GridSearch("sixClusters", tasks)
                .execute()
                .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
    }
}
