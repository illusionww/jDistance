package com.jdistance.workflow;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractGridSearchResult implements Serializable {
    protected String name;
    protected Map<String, Map<Double, Pair<Double, Double>>> data;

    public AbstractGridSearchResult(String name, Map<String, Map<Double, Pair<Double, Double>>> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Map<String, Map<Double, Pair<Double, Double>>> getData() {
        return data;
    }

    protected AbstractGridSearchResult writeData(Writer outputWriter) throws IOException {
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
            for (String lineName : lineNames) {
                Pair<Double, Double> point = data.get(lineName).get(key);
                outputWriter.write(point != null
                        ? point.getLeft() + "\t" + point.getRight() + "\t"
                        : "\t\t"
                );
            }
            outputWriter.write("\n");
        }
        outputWriter.write("\n");

        return this;
    }
}
