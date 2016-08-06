package com.jdistance.local.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.AbstractGridSearch;
import com.jdistance.workflow.Task;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GridSearch extends AbstractGridSearch {
    private static final Logger log = LoggerFactory.getLogger(GridSearch.class);
    private static final String SPACE25 = "                         ";

    public GridSearch() {
    }

    public GridSearch(String name) {
        super(name);
    }

    @Override
    public GridSearch addLine(String lineName, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, GraphBundle graphs, int pointsCount) {
        super.addLine(lineName, estimator, metricWrapper, scorer, graphs, pointsCount);
        return this;
    }

    @Override
    public GridSearch addLinesForDifferentMeasures(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        super.addLinesForDifferentMeasures(estimator, scorer, metricWrappers, graphs, pointsCount);
        return this;
    }

    @Override
    public GridSearchResult execute() {
        Collections.shuffle(tasks);

        Instant startGridSearch = Instant.now();
        log.info("START GRID SEARCH \"{}\"", name);
        log.info("Total {} tasks", tasks.size());

        Stream<Task> stream = Context.getInstance().isParallelTasks() ? tasks.parallelStream() : tasks.stream();
        AtomicInteger counter = new AtomicInteger();
        counter.set(0);
        stream.forEach(task -> {
            Instant startTaskTime = Instant.now();
            Pair<Double, Double> result = task.execute();
            Instant endTaskTime = Instant.now();
            log.debug("{}\t{}  param={}  mean={}  sigma={}  time={}",
                    counter.incrementAndGet(),
                    task.getLineName() + SPACE25.substring(task.getLineName().length()),
                    String.format("%.4f", task.getParam()),
                    result != null ? String.format("%.4f", result.getLeft()) : "null  ",
                    result != null ? String.format("%.4f", result.getRight()) : "null  ",
                    Duration.between(startTaskTime, endTaskTime).toString().substring(2));
        });

        Instant finishGridSearch = Instant.now();
        log.info("GRID SEARCH DONE. Total time: {}", Duration.between(startGridSearch, finishGridSearch).toString().substring(2));
        log.info("----------------------------------------------------------------------------------------------------");

        return new GridSearchResult(name, prepareResults());
    }

    private Map<String, Map<Double, Pair<Double, Double>>> prepareResults() {
        return tasks.stream()
                .collect(Collectors.groupingBy(Task::getLineName))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .collect(Collectors.toMap(Task::getParam, task -> new ImmutablePair<>(task.getMean(), task.getSigma())))));
    }

}
