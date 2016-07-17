package com.jdistance.local.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.Task;
import com.jdistance.workflow.TaskPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalTaskPool extends TaskPool {
    private static final Logger log = LoggerFactory.getLogger(LocalTaskPool.class);

    public LocalTaskPool() {
    }

    public LocalTaskPool(String name) {
        super(name);
    }

    public LocalTaskPool(String name, List<Task> tasks) {
        super(name, tasks);
    }

    public LocalTaskPool(String name, Task... tasks) {
        super(name, tasks);
    }

    public LocalTaskPool addTask(Estimator estimator, Scorer scorer, AbstractMeasureWrapper metricWrapper, GraphBundle graphs, Integer pointsCount) {
        String taskName = estimator.getName() + " " + scorer.getName() + " " + metricWrapper.getName();
        Task task = new Task(taskName,
                new LocalGridSearch(taskName, estimator, metricWrapper, scorer, 0.0, 1.0, pointsCount, Context.getInstance().isParallelGrid()),
                graphs);
        tasks.add(task);
        return this;
    }

    @Override
    public LocalTaskPool buildSimilarTasks(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        super.buildSimilarTasks(estimator, scorer, metricWrappers, graphs, pointsCount);
        return this;
    }

    public LocalTaskPoolResult execute() {
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
                .collect(Collectors.toMap(Task::getName, task -> ((LocalGridSearch) task.getGridSearch()).getScores()));
        return new LocalTaskPoolResult(name, taskNames, data);
    }
}
