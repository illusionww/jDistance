package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.adapter.GNUPlotAdapter;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import com.jdistance.learning.NullEstimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.Ward;
import com.jdistance.metric.Kernel;
import com.jdistance.metric.KernelWrapper;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.style.Smooth;

import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        Context.fill(false, true, true, "./results/data", "./results/img");
        test();
    }

    private static void test() {
        int clustersCount = 2;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, clustersCount, 0.3, 0.1));
        TaskPool pool = new TaskPool("test");
        for (MetricWrapper metric : Metric.getDefaultDistances()) {
            pool.addTask(new Task("Ward metric " + metric.getName(), new Ward(clustersCount), Scorer.RATE_INDEX, metric, graphs, 51));
            pool.addTask(new Task("Diff metric " + metric.getName(), new NullEstimator(), Scorer.DIFFUSION, metric, graphs, 51));
        }
        for (KernelWrapper kernel : Kernel.getDefaultKernels()) {
            pool.addTask(new Task("Ward kernel " + kernel.getName(), new Ward(clustersCount), Scorer.RATE_INDEX, kernel, graphs, 51));
            pool.addTask(new Task("Diff kernel " + kernel.getName(), new NullEstimator(), Scorer.DIFFUSION, kernel, graphs, 51));
        }
        for (KernelWrapper kernel : Kernel.getDefaultSquaredKernels()) {
            pool.addTask(new Task("Ward kernel " + kernel.getName() + " squared", new Ward(clustersCount), Scorer.RATE_INDEX, kernel, graphs, 51));
            pool.addTask(new Task("Diff kernel " + kernel.getName() + " squared", new NullEstimator(), Scorer.DIFFUSION, kernel, graphs, 51));
        }
        TaskPoolResult result = pool.execute();
        result.writeData();

        GNUPlotAdapter gnuplot = new GNUPlotAdapter();
        for (MetricWrapper metric : Metric.getDefaultDistances()) {
            Map<String, Map<Double, Double>> dataForPicture = new TreeMap<>();
            dataForPicture.put(metric.getName() + " metric, Ward", result.getData().get("Ward metric " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel, Ward", result.getData().get("Ward kernel " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel squared, Ward", result.getData().get("Ward kernel " + metric.getName() + " squared"));
            dataForPicture.put(metric.getName() + " metric, Diff", result.getData().get("Diff metric " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel, Diff", result.getData().get("Diff kernel " + metric.getName()));
            dataForPicture.put(metric.getName() + " kernel squared, Diff", result.getData().get("Diff kernel " + metric.getName() + " squared"));
            gnuplot.draw(dataForPicture, "Ward and Diff metric and kernel " + metric.getName() + ", n=100, k=2, pIn=0.3, pOut=0.1 UNIQUE", "[0:1]", "0.2", "[0.3:1]", "0.2", Smooth.UNIQUE);
        }
    }
}

