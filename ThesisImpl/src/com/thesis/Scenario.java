package com.thesis;

import com.thesis.adapter.generator.GraphBundle;
import com.thesis.metric.Distance;
import com.thesis.metric.DistanceClass;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.Checker;
import com.thesis.workflow.checker.ClassifierChecker;
import com.thesis.workflow.task.ClassifierBestParamTask;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Scenario {
    private static final Logger log = LoggerFactory.getLogger(Scenario.class);

    public static TaskChain defaultTasks(Checker checker, List<Distance> distances, double step) {
        String name = checker.getName() + ", step=" + step + ", " + Context.getInstance().SCALE;
        TaskChain chain = new TaskChain(name);
        List<Task> tasks = new ArrayList<>();
        distances.forEach(distance -> {
            Checker checkerClone = checker.clone();
            tasks.add(new DefaultTask(checkerClone, distance, step));
        });
        return chain.addTasks(tasks);
    }
}
