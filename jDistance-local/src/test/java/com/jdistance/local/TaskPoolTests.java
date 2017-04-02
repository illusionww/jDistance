package com.jdistance.local;

import com.jdistance.Dataset;
import com.jdistance.graph.Graph;
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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class TaskPoolTests {
    @Before
    public void init() {
        Context.fill(false, "./test", "./test");
    }

    @Test
    public void tableTest() {
        GraphBundle graphs = Dataset.POLBOOKS.get();
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.RI)
                .setGraphBundles(graphs)
                .setMeasures(Kernel.getAllH_plusRSP_FE())
                .setMeasureParams(0.1)
                .build();
        Map<Task, Pair<Double, Double>> data = new GridSearch(tasks)
                .execute()
                .getData();

        Map<String, Pair<Double, Double>> shortResult = new HashMap<>();
        for(Map.Entry<Task, Pair<Double, Double>> entry : data.entrySet()) {
            Pair<Double, Double> valueNow = shortResult.getOrDefault(entry.getKey().getMeasure().getName(), new ImmutablePair<>(-1., -1.));
            if (entry.getKey().getResult().getLeft() != null && valueNow.getRight() < entry.getKey().getResult().getLeft()) {
                shortResult.put(entry.getKey().getMeasure().getName(), new ImmutablePair<>(entry.getKey().getMeasureParam(), entry.getKey().getResult().getLeft()));
            }
        }
        for (Map.Entry<String, Pair<Double, Double>> entry : shortResult.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue().getLeft() + "\t" + entry.getValue().getRight());
        }

    }

    @Test
    public void calcTest() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, 2, 0.3, 0.1));
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.RI)
                .setGraphBundles(graphs)
                .setMeasures(
                        new KernelWrapper(Kernel.LOG_COMM_H)
                )
                .linspaceMeasureParams(51)
                .build();
        new GridSearch(tasks)
                .execute()
                .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE)
                .draw(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
    }

    @Test
    public void datasetTest() {
        GraphBundle graphs = Dataset.ZACHARY.get();
        List<Task> tasks = new CartesianTaskListBuilder()
                .setEstimators(Estimator.WARD)
                .setScorers(Scorer.RI)
                .setGraphBundles(graphs)
                .setMeasures(
                        new KernelWrapper(Kernel.LOG_COMM_H)
                )
                .linspaceMeasureParams(51)
                .build();
        new GridSearch(tasks)
                .execute()
                .writeData(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE)
                .draw(Axis.MEASURE_PARAM, Axis.MEASURE, Collapse.CHECK_ONLY_ONE);
    }
}
