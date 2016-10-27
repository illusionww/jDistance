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
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, 100, 2, 0.3, 0.1));
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(graphs)
                .setMeasures(
                        new KernelWrapper(Kernel.COMM_H),
                        new KernelWrapper(Kernel.LOG_COMM_H)
                )
                .linspaceMeasureParams(55)
                .build();
        new GridSearch(tasks)
                .execute()
                .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE)
                .draw(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
    }

    public void datasets() {
        List<GraphBundle> datasets = Arrays.asList(
                Dataset.FOOTBALL.get(),
                Dataset.POLBOOKS.get(),
                Dataset.ZACHARY.get()
        );
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(datasets)
                .setMeasures(
                        new KernelWrapper(Kernel.COMM_H),
                        new KernelWrapper(Kernel.LOG_COMM_H)
                )
                .linspaceMeasureParams(60)
                .build();
        new GridSearch(tasks)
                .execute()
                .writeData(Axis.MEASURE_PARAM, Axis.GRAPHSnMEASURE, Collapse.CHECK_ONLY_ONE);
    }

    public void rejectCurve() {
        GraphBundle graphs4param = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(50, 100, 2, 0.3, 0.1));
        GraphBundle graphs4curve = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.1));

        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(graphs4param)
                .setMeasures(Kernel.getAllH_plusRSP_FE())
                .linspaceMeasureParams(55)
                .build();
        new GridSearch(tasks)
                .execute()
                .getData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE)
                .forEach((key, value) -> {
                    KernelWrapper kernel = new KernelWrapper(Kernel.getByName(key));
                    String bestParamString = value.entrySet().stream()
                            .filter(entry -> entry.getValue() != null)
                            .max(Comparator.comparingDouble(Map.Entry::getValue)).get().getKey();
                    Double bestParam = Double.valueOf(bestParamString);
                    log.info("best param for " + key + " is " + bestParamString);
                    log.info("calculate reject curve");
                    RejectCurve rq = new RejectCurve();
                    Map<String, Map<Double, Double>> result = rq.calcCurve(kernel, bestParam, graphs4curve, 200);
                    log.info("save 'rq " + key + ".csv'");
                    rq.writeData(result, new ArrayList<>(result.keySet()), "rq " + key);
                });
    }

    public void rejectCurve2() {
        GraphBundle graphs4curve = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(150, 100, 2, 0.3, 0.1));

        Map<String, Double> bestParams = new HashMap<String, Double>() {{
            put("logHeat", 0.38);
            put("Heat", 0.76);
            put("logFor", 0.64);
            put("FE", 0.94);
            put("SP-CT", 0.28);
            put("Walk", 0.72);
            put("Comm", 0.36);
            put("RSP", 0.99);
            put("pWalk", 0.74);
            put("SCCT", 0.04);
            put("SCT", 0.99);
            put("logComm", 0.56);
            put("For", 0.99);
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
}

