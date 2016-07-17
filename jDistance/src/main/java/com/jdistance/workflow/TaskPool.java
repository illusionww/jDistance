package com.jdistance.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.measure.AbstractMeasureWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class TaskPool implements Serializable {
    protected String name;
    protected List<Task> tasks;

    public TaskPool() {
        this.tasks = new ArrayList<>();
    }

    public TaskPool(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public TaskPool(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public TaskPool(String name, Task... tasks) {
        this.name = name;
        this.tasks = Arrays.asList(tasks);
    }

    protected TaskPool addTask(Task task) {
        tasks.add(task);
        return this;
    }

    protected abstract TaskPool addTask(Estimator estimator, Scorer scorer, AbstractMeasureWrapper metricWrapper, GraphBundle graphs, Integer pointsCount);


    protected TaskPool buildSimilarTasks(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        metricWrappers.forEach(metricWrapper -> {
            addTask(estimator, scorer, metricWrapper, graphs, pointsCount);
        });

        if (name == null) {
            name = estimator.getName() + " - " +
                    graphs.getProperties().getNodesCount() + " nodes, " +
                    graphs.getProperties().getClustersCount() + " clusters, " +
                    "pIn=" + graphs.getProperties().getP_in() + ", " +
                    "pOut=" + graphs.getProperties().getP_out() + ", " +
                    graphs.getProperties().getGraphsCount() + " graphs";
        }
        return this;
    }

    protected abstract TaskPoolResult execute();
}
