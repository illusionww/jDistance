package com.jdistance.impl;

import com.jdistance.impl.workflow.TaskChain;
import com.jdistance.impl.workflow.checker.Checker;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ScenarioHelper {
    private static final Logger log = LoggerFactory.getLogger(ScenarioHelper.class);

    public static TaskChain defaultTasks(Checker checker, List<MetricWrapper> metricWrappers, Integer pointsCount) {
        String name = checker.getName() + ", pointsCount=" + pointsCount;
        TaskChain chain = new TaskChain(name);
        List<Task> tasks = new ArrayList<>();
        metricWrappers.forEach(metricWrapper -> {
            Checker checkerClone = checker.clone();
            tasks.add(new DefaultTask(checkerClone, metricWrapper, pointsCount));
        });
        return chain.addTasks(tasks);
    }
}
