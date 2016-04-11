package com.jdistance.impl.scenarios;

import com.jdistance.graph.GraphBundle;
import com.jdistance.impl.workflow.TaskChainBuilder;
import com.jdistance.metric.Kernel;
import com.jdistance.metric.Metric;

public class Default {
    public static void diffusion_metrics(GraphBundle graphs, int pointsCount) {
        new TaskChainBuilder(Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier("[0.49:1]", "0.1")
                .writeStatistics();
    }

    public static void diffusion_kernels(GraphBundle graphs, int pointsCount) {
        new TaskChainBuilder(Kernel.getDefaultKernels(), pointsCount)
                .setGraphs(graphs).generateDiffusionTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier("[0.49:1]", "0.1")
                .writeStatistics();
    }

    public static void ward_metrics(GraphBundle graphs, int pointsCount, String yrange, String yticks) {
        new TaskChainBuilder(Metric.getDefaultDistances(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier(yrange, yticks)
                .writeStatistics();
    }

    public static void ward_kernels(GraphBundle graphs, int pointsCount, String yrange, String yticks) {
        new TaskChainBuilder(Kernel.getDefaultKernels(), pointsCount)
                .setGraphs(graphs).generateWardTasks().build().execute()
                .writeData()
                .drawUniqueAndBezier(yrange, yticks)
                .writeStatistics();
    }


}
