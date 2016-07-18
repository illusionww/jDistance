package com.jdistance.local.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.Task;
import com.jdistance.workflow.AbstractTaskPool;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskPool extends AbstractTaskPool {
    private static final Logger log = LoggerFactory.getLogger(TaskPool.class);

    public TaskPool() {
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
                new GridSearch(taskName, estimator, metricWrapper, scorer, 0.0, 1.0, pointsCount, Context.getInstance().isParallelGrid()),
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
        Instant startPoolTime = Instant.now();
        log.info("START TASK POOL \"{}\"", name);

        Stream<Task> stream = Context.getInstance().isParallelTasks() ? tasks.parallelStream() : tasks.stream();
        stream.forEach(task -> {
            Instant startTaskTime = Instant.now();
            log.info("LocalTask START: {}", task.getName());
            task.execute();
            Instant endTaskTime = Instant.now();
            log.info("LocalTask DONE: {}. Time: {} ", task.getName(), Duration.between(startTaskTime, endTaskTime));
        });

        Instant finishPoolTime = Instant.now();
        log.info("TASK POOL DONE. Time: {}", Duration.between(startPoolTime, finishPoolTime));
        log.info("----------------------------------------------------------------------------------------------------");

        List<String> taskNames = tasks.stream().map(Task::getName)
                .collect(Collectors.toList());
        Map<String, Map<Double, Double>> data = tasks.stream()
                .collect(Collectors.toMap(Task::getName, task -> ((GridSearch) task.getGridSearch()).getScores()));
        return new TaskPoolResult(name, taskNames, data);
    }
}
