package com.jdistance.local.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.AbstractTaskPool;
import com.jdistance.workflow.Task;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskPool extends AbstractTaskPool {
    private static final Logger log = LoggerFactory.getLogger(TaskPool.class);

    @Override
    public TaskPool addLine(String lineName, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, GraphBundle graphs, int pointsCount) {
        super.addLine(lineName, estimator, metricWrapper, scorer, graphs, pointsCount);
        return this;
    }

    @Override
    public TaskPool addLinesForDifferentMeasures(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        super.addLinesForDifferentMeasures(estimator, scorer, metricWrappers, graphs, pointsCount);
        return this;
    }

    @Override
    public TaskPoolResult execute() {
        Instant startPoolTime = Instant.now();
        log.info("START TASK POOL \"{}\"", name);
        log.info("Total count: {}", tasks.size());

        Stream<Task> stream = Context.getInstance().isParallelTasks() ? tasks.parallelStream() : tasks.stream();
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);
        stream.forEach(task -> {
            Instant startTaskTime = Instant.now();
            task.execute();
            Instant endTaskTime = Instant.now();
            log.debug("{}\t{}\t{}\ttime={}", counter.incrementAndGet(), task.getLineName(), String.format("%.4f", task.getParam()), Duration.between(startTaskTime, endTaskTime).toString().substring(2));
        });

        Instant finishPoolTime = Instant.now();
        log.info("TASK POOL DONE. Total time: {}", Duration.between(startPoolTime, finishPoolTime).toString().substring(2));
        log.info("----------------------------------------------------------------------------------------------------");

        return new TaskPoolResult(name, prepareResults());
    }

    private Map<String, Map<Double, Pair<Double, Double>>> prepareResults() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getLineName))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .collect(Collectors.toMap(Task::getParam, task -> new ImmutablePair<>(task.getMean(), task.getSigma())))));
    }

}
