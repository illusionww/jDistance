package com.jdistance.spark.workflow;

import com.jdistance.workflow.AbstractGridSearch;
import com.jdistance.workflow.Task;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.AccumulatorParam;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.LongAccumulator;

public class GridSearch extends AbstractGridSearch {
    private static final Logger log = LoggerFactory.getLogger(GridSearch.class);

    public GridSearch(List<Task> tasks) {
        super(tasks);
    }

    @Override
    public GridSearchResult execute() {
        Collections.shuffle(tasks);

        Instant startGridSearch = Instant.now();
        log.info("START GRID SEARCH \"{}\"", name);
        log.info("Total {} tasks", tasks.size());

        JavaRDD<Task> parallelizeTasks = Context.getInstance().getSparkContext().parallelize(tasks);
        Map<Task, Pair<Double, Double>> rawData = parallelizeTasks
                .mapToPair(task -> new Tuple2<>(task, task.execute()))
                .collectAsMap();

        Instant finishGridSearch = Instant.now();
        log.info("GRID SEARCH DONE. Total time: {}", Duration.between(startGridSearch, finishGridSearch).toString().substring(2));

        return new GridSearchResult(name, rawData);
    }
}
