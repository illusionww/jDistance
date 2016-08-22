package com.jdistance.workflow;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;

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

        List<String> headers = new ArrayList<>();
        headers.add("param");
        for (String taskName : data.keySet()) {
            headers.add(taskName + "_mean");
            headers.add(taskName + "_sigma");
        }

        Map<Double, List<Double>> outputData = new TreeMap<>();
        for (Double key : points) {
            List<Double> row = new ArrayList<>();
            for (String lineName : lineNames) {
                Pair<Double, Double> point = data.get(lineName).get(key);
                row.add(point != null ? point.getLeft() : null);
                row.add(point != null ? point.getRight() : null);
            }
            outputData.put(key, row);
        }

        write(headers, outputData, outputWriter);
        return this;
    }

    private AbstractGridSearchResult write(List<String> headers, Map<?, List<Double>> outputData, Writer outputWriter) throws IOException {
        for (String header : headers) {
            outputWriter.write(header + "\t");
        }
        outputWriter.write("\n");
        for (Map.Entry<?, List<Double>> row : outputData.entrySet()) {
            outputWriter.write(row.getKey().toString() + "\t");
            for (Double cell : row.getValue()) {
                outputWriter.write((cell != null ? cell.toString() : "") + "\t");
            }
            outputWriter.write("\n");
        }
        outputWriter.write("\n");
        return this;
    }
}
