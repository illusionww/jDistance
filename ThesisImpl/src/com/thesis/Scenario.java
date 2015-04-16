package com.thesis;

import com.thesis.metric.Distance;
import com.thesis.workflow.Context;
import com.thesis.workflow.TaskChain;
import com.thesis.workflow.checker.Checker;
import com.thesis.workflow.task.DefaultTask;
import com.thesis.workflow.task.Task;

import java.util.ArrayList;
import java.util.List;

public class Scenario {


    public static TaskChain defaultTasks(Checker checker, List<Distance> distances, Double step) {
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
