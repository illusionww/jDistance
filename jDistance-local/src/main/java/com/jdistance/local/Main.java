package com.jdistance.local;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Axis;
import com.jdistance.learning.Collapse;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.DistanceWrapper;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.KernelWrapper;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.GridSearch;
import com.jdistance.workflow.CartesianTaskListBuilder;
import com.jdistance.workflow.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Context.fill(true, ".\\results-local\\data", ".\\results-local\\img");
        if (args.length == 1) {
            String methodName = args[0];
            Class<?> clazz = Class.<Main>forName("com.jdistance.local.Main");
            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);
            log.info("Run job \"" + methodName + "\"");
            method.invoke(new Main());
            log.info("Done job \"" + methodName + "\"");
        } else {
            throw new RuntimeException("There is no task param!");
        }
    }

    public void trivial() {
        for (double pOut: Arrays.asList(0.1)) {
            GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, 2, 0.3, pOut));
            List<Task> tasks = new CartesianTaskListBuilder()
                    .setEstimators(Estimator.WARD)
                    .setScorers(Scorer.ARI)
                    .setGraphBundles(graphs)
                    .setMeasures(
                            new KernelWrapper(Kernel.HEAT_H),
                            new KernelWrapper(Kernel.LOG_HEAT_H)
                    )
                    .linspaceMeasureParams(55)
                    .build();
            new GridSearch(tasks)
                    .execute()
                    .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE)
                    .draw(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
        }
    }

    public void rejectCurve2() {
        GraphBundle graphs4curve = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.1));

        Map<String, Double> bestParams = new HashMap<String, Double>() {{
            put("pWalk", 0.90);
            put("Walk", 0.74);
            put("For", 0.98);
            put("logFor", 0.56);
            put("Comm", 0.32);
            put("logComm", 0.54);
            put("Heat", 0.78);
            put("logHeat", 0.36);
            put("SCT", 0.62);
            put("SCCT", 0.26);
            put("RSP", 0.98);
            put("FE", 0.94);
            put("SP-CT", 0.34);
        }};
        bestParams.forEach((key, value) -> {
            DistanceWrapper kernel = new DistanceWrapper(Distance.getByName(key));
            log.info("best param for " + key + " is " + value);
            log.info("calculate reject curve");
            RejectCurve rq = new RejectCurve();
            Map<String, Map<Double, Double>> result = rq.calcCurve(kernel, value, graphs4curve, 200);
            log.info("save 'rq " + key + ".csv'");
            rq.writeData(result, new ArrayList<>(result.keySet()), "rq " + key);
        });
    }

    public static void datasets() {
        List<Dataset> datasets = Arrays.asList(
//                Dataset.FOOTBALL,
                Dataset.POLBOOKS
//                Dataset.ZACHARY,
//                Dataset.news_2cl_1,
//                Dataset.news_2cl_2,
//                Dataset.news_2cl_3,
//                Dataset.news_3cl_1,
//                Dataset.news_3cl_2,
//                Dataset.news_3cl_3,
//                Dataset.news_5cl_1,
//                Dataset.news_5cl_2,
//                Dataset.news_5cl_3
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

    public static void competitions() {
        Map<String, List<GraphBundle>> graphBundles = new HashMap<>();
//        graphBundles.put("n100c4pout015",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 4, 0.3, 0.15)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
//        graphBundles.put("n100c2pout015",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.15)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
//        graphBundles.put("n100c2pout01",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.1)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
//        graphBundles.put("n100c4pout01",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 4, 0.3, 0.1)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
//        graphBundles.put("n200c4pout015",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 4, 0.3, 0.15)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );
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
//        graphBundles.put("n200c4pout01",
//                new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 200, 4, 0.3, 0.1)).getGraphs().stream()
//                        .map(graph -> graph.toBundle(UUID.randomUUID().toString()))
//                        .collect(Collectors.toList())
//        );

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


}

