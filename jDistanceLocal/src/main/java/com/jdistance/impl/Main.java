package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.Ward;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;

public class Main {
    public static void main(String[] args) {
        Context.fill(false, true, true, "./results/data", "./results/img");
        test2();
    }

    private static void test() {
        int clustersCount = 2;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, clustersCount, 0.3, 0.1));
        Task task = new Task(new Ward(clustersCount), Scorer.RATE_INDEX, new MetricWrapper(Metric.FOREST), graphs, 51);
        new TaskPool("test", task).execute().writeData().writeStatistics().drawUniqueAndBezier("[0:1]", "0.2");
    }

    private static void test2() {
        int clustersCount = 2;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, clustersCount, 0.3, 0.1));
        TaskPoolResult result = new TaskPool().buildSimilarTasks(new Ward(clustersCount), Scorer.RATE_INDEX, Metric.getDefaultDistances(), graphs, 51).execute();
        result.writeData().writeStatistics().drawUniqueAndBezier("[0:1]", "0.2");
    }
}

