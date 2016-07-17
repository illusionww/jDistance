package com.jdistance.local.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.gridsearch.GridSearch;
import com.jdistance.measure.AbstractMeasureWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LocalGridSearch extends GridSearch {
    private Map<Double, Double> scores = new ConcurrentHashMap<>();
    private boolean isParallel;

    public LocalGridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount, boolean isParallel) {
        super(name, estimator, metricWrapper, scorer, from, to, pointsCount);
        this.isParallel = isParallel;
    }

    public void fit(GraphBundle graphs) {
        this.graphs = graphs;

        Stream<Double> paramStream = isParallel ? paramGrid.parallelStream() : paramGrid.stream();
        paramStream.forEach(idx -> {
            Double score = validate(idx, metricWrapper);
            System.out.println(name + "\t" + String.format("%1.5f", idx) + "\t" + score);
            if (score != null) {
                scores.put(idx, score);
            }
        });
    }

    public Map<Double, Double> getScores() {
        return scores;
    }
}
