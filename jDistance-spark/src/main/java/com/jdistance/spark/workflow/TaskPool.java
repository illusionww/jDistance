package com.jdistance.spark.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.Task;
import com.jdistance.workflow.AbstractTaskPool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskPool extends AbstractTaskPool {
    public TaskPool() {
        super();
    }

    public TaskPool(String name) {
        super(name);
    }

    public TaskPool(String name, List<Task> tasks) {
        super(name, tasks);
    }

    public TaskPool(String name, Task... tasks) {
        super(name, tasks);
    }

    public TaskPool addTask(Estimator estimator, Scorer scorer, AbstractMeasureWrapper metricWrapper, GraphBundle graphs, Integer pointsCount) {
        String taskName = estimator.getName() + " " + scorer.getName() + " " + metricWrapper.getName();
        Task task = new Task(taskName,
                new GridSearch(taskName, estimator, metricWrapper, scorer, 0.0, 1.0, pointsCount),
                graphs);
        tasks.add(task);
        return this;
    }

    @Override
    public TaskPool buildSimilarTasks(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        super.buildSimilarTasks(estimator, scorer, metricWrappers, graphs, pointsCount);
        return this;
    }

    public TaskPoolResult execute() {
        tasks.forEach(Task::execute);
        List<String> taskNames = tasks.stream()
                .map(Task::getName)
                .collect(Collectors.toList());
        Map<String, Map<Double, Double>> data = tasks.stream()
                .collect(Collectors.toMap(Task::getName, task -> ((GridSearch) task.getGridSearch()).getScores().collectAsMap()));
        return new TaskPoolResult(name, taskNames, data);
    }
}
