package com.jdistance.workflow;

import com.jdistance.learning.Axis;
import com.jdistance.learning.Collapse;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public abstract class AbstractGridSearchResult implements Serializable {
    protected String name;
    protected Map<Task, Pair<Double, Double>> data;

    public AbstractGridSearchResult(String name, Map<Task, Pair<Double, Double>> data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public Map<Task, Pair<Double, Double>> getData() {
        return data;
    }

    public Map<String, Map<String, Double>> getData(Axis xAxis, Axis lines, Collapse collapse) {
        return prepareData(xAxis, lines, collapse);
    }

    protected AbstractGridSearchResult writeData(Axis xAxis, Axis lines, Collapse collapse, Writer outputWriter) throws IOException {
        Set<String> xValues = data.keySet().stream().map(xAxis::getFromTask).collect(Collectors.toSet());
        Set<String> linesNames = data.keySet().stream().map(lines::getFromTask).collect(Collectors.toSet());

        List<String> headers = new ArrayList<>();
        headers.add("param");
        headers.addAll(linesNames);

        Map<String, List<Double>> outputData = new TreeMap<>();
        Map<String, Map<String, Double>> preparedData = prepareData(xAxis, lines, collapse);
        for (String xValue : xValues) {
            List<Double> row = new ArrayList<>();
            for (String lineName : linesNames) {
                row.add(preparedData.get(lineName).get(xValue));
            }
            outputData.put(xValue, row);
        }

        write(headers, outputData, outputWriter);
        return this;
    }

    protected Map<String, Map<String, Double>> prepareData(Axis xAxis, Axis lines, Collapse collapse) {
        Set<String> linesNames = data.keySet().stream().map(lines::getFromTask).collect(Collectors.toSet());
        Set<String> xValues = data.keySet().stream().map(xAxis::getFromTask).collect(Collectors.toSet());

        Map<String, Map<String, Double>> result = new HashMap<>();
        for (String lineName : linesNames) {
            Map<String, Double> lineData = new HashMap<>();
            for (String xValue : xValues) {
                DoubleStream stream = data.keySet().stream()
                        .filter(task -> lineName.equals(lines.getFromTask(task)) &&
                                xValue.equals(xAxis.getFromTask(task)) &&
                                task.getResult() != null && task.getResult().getLeft() != null)
                        .mapToDouble(task -> task.getResult().getLeft());
                OptionalDouble value = collapse.apply(stream);
                lineData.put(xValue, value.isPresent() ? value.getAsDouble() : null);
            }
            result.put(lineName, lineData);
        }
        return result;
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
