package com.jdistance.impl.workflow.task;

import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class DefaultTask extends Task {
    private static final Logger log = LoggerFactory.getLogger(DefaultTask.class);

    private Distance distance;
    private Checker checker;
    private Integer pointsCount;
    private Map<Double, Double> result;

    public DefaultTask(Checker checker, Distance distance, Integer pointsCount) {
        this.distance = distance;
        this.checker = checker;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return DistanceClass.getDistanceName(distance) + " " + checker.getName() + ", pointsCount=" + pointsCount + " " + distance.getScale();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        Map<Double, Double> distanceResult = checker.seriesOfTests(distance, 0.00001, 0.99999, pointsCount);
        result = removeNaN(distanceResult);
        return this;
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }

    private Map<Double, Double> removeNaN(Map<Double, Double> distanceResult) {
        return distanceResult.entrySet().stream().filter(entry -> !Double.isNaN(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
