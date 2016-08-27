package com.jdistance.local;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Axis;
import com.jdistance.learning.Collapse;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.KernelWrapper;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.GridSearch;
import com.jdistance.workflow.CartesianTaskListBuilder;
import com.jdistance.workflow.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.25, 0.1));
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.ARI)
                .setGraphBundles(graphs)
                .setMeasures(
                        new KernelWrapper(Kernel.COMM_H),
                        new KernelWrapper(Kernel.LOG_COMM_H)
                )
                .linspaceMeasureParams(60)
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

    public void size() {
        List<GraphBundle> graphBundles = new ArrayList<>();
        int sum = 100;
        int step = 5;
        for (int first = step; first <= sum/2; first += step) {
            int second = sum - first;
            graphBundles.add(new GnPInPOutGraphGenerator().generate(Double.toString(first), new GeneratorPropertiesPOJO(sum, new int[]{
                    first, second
            }, new double[][]{
                    {0.3, 0.1},
                    {0.1, 0.3}
            })));
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

