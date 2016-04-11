package com.jdistance.impl;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.scenarios.Default;
import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.gridsearch.clusterer.WardGridSearch;
import com.jdistance.impl.workflow.task.CustomTask;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Kernel;
import com.jdistance.metric.KernelWrapper;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.style.Smooth;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        standard();
    }

    private static void standard() {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(10, 100, 3, 0.3, 0.1);
        GraphBundle graphs = GnPInPOutGraphGenerator.getInstance().generate(properties);
        Default.ward_metrics(graphs, 51, "[0.3:1]", "0.2");
        Default.ward_kernels(graphs, 51, "[0.3:1]", "0.2");
    }

    private static void forest() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 2; i < 6; i++) {
            GraphBundle graphs = GnPInPOutGraphGenerator.getInstance().generate(new GeneratorPropertiesDTO(10, 150, i, 0.3, 0.1));
            tasks.add(new CustomTask(new WardGridSearch(graphs, graphs.getProperties().getClustersCount()), new KernelWrapper(i + " clusters", Kernel.FOREST), 0.0, 40.0, 41));
        }
        new TaskChain("kernel forest", tasks).execute().draw("kernel forest 150", "[0:40]", "5", "[0.2:1]", "0.2", Smooth.UNIQUE);
    }
}

