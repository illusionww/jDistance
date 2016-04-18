package com.jdistance.impl.workflow;

import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.gridsearch.MetricStatistics;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.metric.AbstractDistanceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskPool {
    private static final Logger log = LoggerFactory.getLogger(TaskPool.class);

    private String name;
    private List<Task> tasks;

    public TaskPool() {
        this.tasks = new ArrayList<>();
    }

    public TaskPool(String name) {
        this.name = name;
        this.tasks = new ArrayList<>();
    }

    public TaskPool(String name, Task... tasks) {
        this.name = name;
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public TaskPool(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = new ArrayList<>(tasks);
    }

    public String getName() {
        return name;
    }

    public TaskPool addTask(Task task) {
        this.tasks.add(task);
        return this;
    }

    public TaskPool addTasks(List<Task> tasks) {
        this.tasks.addAll(tasks);
        return this;
    }

    public TaskPool buildSimilarTasks(Estimator estimator, Scorer scorer, List<? extends AbstractDistanceWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        metricWrappers.forEach(metricWrapper -> {
            tasks.add(new Task(estimator, scorer, metricWrapper, graphs, pointsCount));
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

    public List<Task> getTasks() {
        return tasks;
    }

    public TaskPoolResult execute() {
        Date start = new Date();
        log.info("START TASK POOL \"{}\"", name);

        Stream<Task> stream = Context.getInstance().isParallelTasks() ? tasks.parallelStream() : tasks.stream();
        stream.forEach(task -> {
            Date startTask = new Date();
            log.info("Task START: {}", task.getName());
            task.execute();
            Date finishTask = new Date();
            long diffTask = finishTask.getTime() - startTask.getTime();
            log.info("Task DONE: {}. Time: {} ", task.getName(), diffTask);
        });

        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("TASK POOL DONE. Time: {}", diff);
        log.info("----------------------------------------------------------------------------------------------------", diff);

        List<String> taskNames = tasks.stream().map(Task::getName)
                .collect(Collectors.toList());
        Map<String, Map<Double, Double>> data = tasks.stream()
                .collect(Collectors.toMap(Task::getName, task -> task.getGridSearch().getScores()));
        Map<String, Map<Graph, Map<Double, MetricStatistics>>> metricStatistics = tasks.stream()
                .collect(Collectors.toMap(Task::getName, task -> task.getGridSearch().getMetricStatistics()));
        return new TaskPoolResult(name, taskNames, data, metricStatistics);
    }
}
