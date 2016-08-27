package com.jdistance.local.workflow;

import com.jdistance.workflow.AbstractGridSearch;
import com.jdistance.workflow.Task;
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

    public GridSearch(List<Task> tasks) {
        super(tasks);
    }

    public GridSearch(String name, List<Task> tasks) {
        super(name, tasks);
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
                    task.getMeasure().getName() + SPACE25.substring(task.getMeasure().getName().length()),
                    String.format("%.4f", task.getMeasureParam()),
                    result.getLeft() != null ? String.format("%.4f", result.getLeft()) : "null  ",
                    result.getRight() != null ? String.format("%.4f", result.getRight()) : "null  ",
                    Duration.between(startTaskTime, endTaskTime).toString().substring(2));
        });

        Instant finishGridSearch = Instant.now();
        log.info("GRID SEARCH DONE. Total time: {}", Duration.between(startGridSearch, finishGridSearch).toString().substring(2));
        log.info("----------------------------------------------------------------------------------------------------");

        Map<Task, Pair<Double, Double>> rawResult = tasks.stream().collect(Collectors.toMap(task -> task, Task::getResult));
        return new GridSearchResult(name, rawResult);
    }

}
