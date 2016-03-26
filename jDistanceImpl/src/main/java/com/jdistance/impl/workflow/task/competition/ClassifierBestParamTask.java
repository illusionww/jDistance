package com.jdistance.impl.workflow.task.competition;

import com.jdistance.impl.workflow.gridsearch.classifier.KNearestNeighborsGridSearch;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ClassifierBestParamTask extends Task {
    private static final Logger log = LoggerFactory.getLogger(ClassifierBestParamTask.class);

    private Double from;
    private Double to;
    private Integer checkerPointsCount;
    private Integer pointsCount;

    public ClassifierBestParamTask(KNearestNeighborsGridSearch checker, MetricWrapper metricWrapper, Double from, Double to, int checkerPointsCount, int pointsCount) {
        this.gridSearch = checker;
        this.metricWrapper = metricWrapper;
        this.from = from;
        this.to = to;
        this.checkerPointsCount = checkerPointsCount;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return metricWrapper.getName() + " " + gridSearch.getName();
    }

    @Override
    public MetricWrapper getMetricWrapper() {
        return metricWrapper;
    }

    @Override
    public Task execute() {
        double step = (to - from) / (pointsCount - 1);
        IntStream.range(0, pointsCount).boxed().collect(Collectors.toList()).forEach(idx -> {
            Double x = from + idx * step;
            ((KNearestNeighborsGridSearch) gridSearch).setX(x);
            log.info("distance {}, x: {}", metricWrapper.getName(), x);
            Task task = new DefaultTask(gridSearch, metricWrapper, checkerPointsCount);
            Map.Entry<Double, Double> best = task.execute().getMaxResult();
            result.put(x, best.getValue());
        });

        return this;
    }

    @Override
    public Map<Double, Double> getResult() {
        return result;
    }
}
