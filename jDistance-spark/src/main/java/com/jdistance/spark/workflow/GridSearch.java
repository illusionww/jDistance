package com.jdistance.spark.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.AbstractGridSearch;
import com.jdistance.workflow.Task;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GridSearch extends AbstractGridSearch {
    private static final Logger log = LoggerFactory.getLogger(GridSearch.class);

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

        JavaRDD<Task> parallelizeTasks = Context.getInstance().getSparkContext().parallelize(tasks);
        Map<Pair<String, Double>, Pair<Double, Double>> rawData = parallelizeTasks
                .mapToPair(task -> new Tuple2<>(task.getKey(), task.execute()))
                .collectAsMap();

        Instant finishGridSearch = Instant.now();
        log.info("GRID SEARCH DONE. Total time: {}", Duration.between(startGridSearch, finishGridSearch).toString().substring(2));

        return new GridSearchResult(name, prepareResults(rawData));
    }

    private Map<String, Map<Double, Pair<Double, Double>>> prepareResults(Map<Pair<String, Double>, Pair<Double, Double>> rawData) {
        return rawData.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey().getLeft()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .filter(entry2 -> entry2.getValue() != null)
                        .collect(Collectors.toMap(entry2 -> entry2.getKey().getRight(), Map.Entry::getValue))));
    }
}
