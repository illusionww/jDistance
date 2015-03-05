package com.thesis.workflow.task;

import com.thesis.metric.Distance;
import com.thesis.workflow.checker.Checker;

import java.util.Map;

public interface Task {
    public Checker getChecker();

    public Task execute(boolean parallel);

    public Map<Distance, Map<Double, Double>> getResults();
}
