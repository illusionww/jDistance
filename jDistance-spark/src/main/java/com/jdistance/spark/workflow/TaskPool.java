package com.jdistance.spark.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.workflow.AbstractTaskPool;
import com.jdistance.workflow.Task;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskPool extends AbstractTaskPool {
    @Override
    public TaskPool addLine(String lineName, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, GraphBundle graphs, double from, double to, int pointsCount) {
        super.addLine(lineName, estimator, metricWrapper, scorer, graphs, from, to, pointsCount);
        return this;
    }

    @Override
    public TaskPool addLinesForDifferentMeasures(Estimator estimator, Scorer scorer, List<? extends AbstractMeasureWrapper> metricWrappers, GraphBundle graphs, Integer pointsCount) {
        super.addLinesForDifferentMeasures(estimator, scorer, metricWrappers, graphs, pointsCount);
        return this;
    }

    @Override
    public TaskPoolResult execute() {
        JavaRDD<Task> parallelizeTasks = Context.getInstance().getSparkContext().parallelize(tasks);
        Map<ImmutablePair<String, Double>, Pair<Double, Double>> rawData = parallelizeTasks
                .mapToPair(task -> new Tuple2<>(new ImmutablePair<>(task.getLineName(), task.getParam()), task.execute()))
                .collectAsMap();
        return new TaskPoolResult(name, prepareResults(rawData));
    }

    private Map<String, Map<Double, Pair<Double, Double>>> prepareResults(Map<ImmutablePair<String, Double>, Pair<Double, Double>> rawData) {
        return rawData.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey().getLeft()))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .collect(Collectors.toMap(entry2 -> entry2.getKey().getRight(), Map.Entry::getValue))));
    }
}
