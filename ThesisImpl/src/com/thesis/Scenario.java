package com.thesis;

import com.thesis.metric.Distance;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.Checker;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    private static final Logger log = LoggerFactory.getLogger(Scenario.class);

    public static TaskChain defaultTasks(Checker checker, List<Distance> distances, Integer pointsCount) {
        String name = checker.getName() + ", pointsCount=" + pointsCount;
        TaskChain chain = new TaskChain(name);
        List<Task> tasks = new ArrayList<>();
        distances.forEach(distance -> {
            Checker checkerClone = checker.clone();
            tasks.add(new DefaultTask(checkerClone, distance, pointsCount));
        });
        return chain.addTasks(tasks);
    }
}
