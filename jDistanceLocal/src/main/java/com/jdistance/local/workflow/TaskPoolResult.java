package com.jdistance.local.workflow;

import com.jdistance.local.adapter.GNUPlotAdapter;
import com.panayotis.gnuplot.style.Smooth;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TaskPoolResult {
    private static final Logger log = LoggerFactory.getLogger(TaskPoolResult.class);

    private String name;
    private List<String> taskNames;
    private Map<String, Map<Double, Double>> data;

    public TaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
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

    public Map<String, Map<Double, Double>> getData() {
        return data;
    }

    public Map.Entry<Double, Double> getBestParam(String taskName) {
        Map<Double, Double> scores = data.get(taskName);
        Optional<Map.Entry<Double, Double>> maxOptional = scores.entrySet().stream()
                .filter(entry -> !entry.getValue().isNaN())
                .max(Map.Entry.comparingByValue(Double::compareTo));
        return maxOptional.isPresent() ? maxOptional.get() : null;
    }

    public Double getQuantile(String taskName, double quantile) {
        return new Percentile().evaluate(data.get(taskName).values().stream()
                .filter(i -> !i.isNaN())
                .mapToDouble(i -> i).toArray(), quantile);
    }

    public TaskPoolResult writeData() {
        writeData(name);
        return this;
    }

    public TaskPoolResult writeData(String filename) {
        log.info("Write data as " + filename);
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(filename, "csv")))) {
            Set<Double> points = new TreeSet<>();
            data.values().forEach(scores -> points.addAll(scores.keySet()));

            outputWriter.write("param\t");
            for (String taskName : taskNames) {
                outputWriter.write(taskName + "\t");
            }
            outputWriter.newLine();
            for (Double key : points) {
                outputWriter.write(key + "\t");
                for (String taskName : taskNames) {
                    outputWriter.write(data.get(taskName).get(key) + "\t");
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            log.error("IOException while write results", e);
        }

        return this;
    }

    public TaskPoolResult drawUnique(String xrange, String xticks, String yrange, String yticks) {
        draw(name, xrange, xticks, yrange, yticks, Smooth.UNIQUE);
        return this;
    }

    public TaskPoolResult drawUnique(String yrange, String yticks) {
        draw(name, "[0:1]", "0.2", yrange, yticks, Smooth.UNIQUE);
        return this;
    }

    public TaskPoolResult drawUniqueAndBezier(String yrange, String yticks) {
        draw(name + ".UNIQUE", "[0:1]", "0.2", yrange, yticks, Smooth.UNIQUE);
        draw(name + ".BEZIER", "[0:1]", "0.2", yrange, yticks, Smooth.BEZIER);
        return this;
    }

    public TaskPoolResult drawUniqueAndBezier(String xrange, String xticks, String yrange, String yticks) {
        draw(name + ".UNIQUE", xrange, xticks, yrange, yticks, Smooth.UNIQUE);
        draw(name + ".BEZIER", xrange, xticks, yrange, yticks, Smooth.BEZIER);
        return this;
    }

    public TaskPoolResult draw(String imgTitle, String yrange, String yticks, Smooth smooth) {
        draw(imgTitle, "[0:1]", "0.2", yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult draw(String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        drawByName(taskNames, imgTitle, xrange, xticks, yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult drawByName(List<String> taskNames, String imgTitle, String yrange, String yticks, Smooth smooth) {
        drawByName(taskNames, imgTitle, "[0:1]", "0.2", yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult drawByName(List<String> taskNames, String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        log.info("Draw " + imgTitle);
        try {
            GNUPlotAdapter ga = new GNUPlotAdapter();
            ga.draw(taskNames, data, imgTitle, xrange, xticks, yrange, yticks, smooth);
        } catch (RuntimeException e) {
            log.error("RuntimeException while write picture", e);
        }
        return this;
    }
}
