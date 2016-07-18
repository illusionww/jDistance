package com.jdistance.workflow;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractTaskPoolResult implements Serializable {
    protected String name;
    protected List<String> taskNames;
    protected Map<String, Map<Double, Double>> data;

    public AbstractTaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
        this.name = name;
        this.taskNames = taskNames;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public List<String> getTaskNames() {
        return taskNames;
    }

    protected AbstractTaskPoolResult writeData(Writer outputWriter) throws IOException {
        Set<Double> points = new TreeSet<>();
        data.values().forEach(scores -> points.addAll(scores.keySet()));

        outputWriter.write("param\t");
        for (String taskName : taskNames) {
            outputWriter.write(taskName + "\t");
        }
        outputWriter.write("\n");
        for (Double key : points) {
            outputWriter.write(key + "\t");
            for (String taskName : taskNames) {
                outputWriter.write(data.get(taskName).get(key) + "\t");
            }
            outputWriter.write("\n");
        }
        return this;
    }
}
