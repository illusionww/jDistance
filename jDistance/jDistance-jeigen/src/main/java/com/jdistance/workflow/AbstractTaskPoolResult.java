package com.jdistance.workflow;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractTaskPoolResult implements Serializable {
    protected String name;
    protected Map<String, Map<Double, Pair<Double, Double>>> data;

    public AbstractTaskPoolResult(String name, Map<String, Map<Double, Pair<Double, Double>>> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    protected AbstractTaskPoolResult writeData(Writer outputWriter) throws IOException {
        Set<String> lineNames = data.keySet();
        Set<Double> points = new TreeSet<>();
        data.values().forEach(scores -> points.addAll(scores.keySet()));

        outputWriter.write("param\t");
        for (String taskName : lineNames) {
            outputWriter.write(taskName + "_mean" + "\t" + taskName + "_sigma" + "\t");
        }
        outputWriter.write("\n");

        for (Double key : points) {
            outputWriter.write(key + "\t");
            for (String taskName : lineNames) {
                Pair<Double, Double> point = data.get(taskName).get(key);
                outputWriter.write(point.getLeft() + "\t" + point.getRight() + "\t");
            }
            outputWriter.write("\n");
        }
        outputWriter.write("\n");

        return this;
    }
}
