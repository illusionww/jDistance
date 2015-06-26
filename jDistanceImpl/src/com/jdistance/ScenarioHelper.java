package com.jdistance;

import com.jdistance.metric.Distance;
import com.jdistance.workflow.TaskChain;
import com.jdistance.workflow.checker.Checker;
import com.jdistance.workflow.task.DefaultTask;
import com.jdistance.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ScenarioHelper {
    private static final Logger log = LoggerFactory.getLogger(ScenarioHelper.class);

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
