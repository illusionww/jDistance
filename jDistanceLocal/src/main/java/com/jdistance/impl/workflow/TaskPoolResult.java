package com.jdistance.impl.workflow;

import com.jdistance.graph.Graph;
import com.jdistance.gridsearch.MetricStatistics;
import com.jdistance.impl.adapter.GNUPlotAdapter;
import com.panayotis.gnuplot.style.Smooth;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TaskPoolResult {
    private static final Logger log = LoggerFactory.getLogger(TaskPoolResult.class);

    private String name;
    private Map<String, Map<Double, Double>> data;
    private Map<String, Map<Graph, Map<Double, MetricStatistics>>> metricStatistics;

    public TaskPoolResult(String name, Map<String, Map<Double, Double>> data, Map<String, Map<Graph, Map<Double, MetricStatistics>>> metricStatistics) {
        this.name = name;
        this.data = data;
        this.metricStatistics = metricStatistics;
    }

    public String getName() {
        return name;
    }

    public Map<String, Map<Double, Double>> getData() {
        return data;
    }

    public TaskPoolResult writeData() {
        writeData(name);
        return this;
    }

    public TaskPoolResult writeData(String filename) {
        log.info("Write data...");
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(filename, "csv")))) {
            Set<Double> points = new TreeSet<>();
            data.values().forEach(scores -> points.addAll(scores.keySet()));

            outputWriter.write("\t");
            for (String taskNames : data.keySet()) {
                outputWriter.write(taskNames + "\t");
            }
            outputWriter.newLine();
            for (Double key : points) {
                outputWriter.write(key + "\t");
                for (Map<Double, Double> scores : data.values()) {
                    outputWriter.write(scores.get(key) + "\t");
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            log.error("IOException while write results", e);
        }

        return this;
    }

    public TaskPoolResult writeStatistics() {
        writeStatistics(name);
        return this;
    }

    public TaskPoolResult writeStatistics(String filename) {
        log.info("Write statistics...");
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(filename + "_metric-statistics", "csv")))) {
            for (Map.Entry<String, Map<Graph, Map<Double, MetricStatistics>>> entry : metricStatistics.entrySet()) {
                outputWriter.write("Metric " + entry.getKey() + "\n");
                for (Map<Double, MetricStatistics> statisticsForGraph : entry.getValue().values()) {
                    List<Map.Entry<Double, MetricStatistics>> sortedList = new ArrayList<>(statisticsForGraph.entrySet());
                    Collections.sort(sortedList, Comparator.comparingDouble(Map.Entry::getKey));

                    outputWriter.write("param\tmin\tmax\tavg\tscore\t");
                    for (Pair<String, String> pair : sortedList.get(0).getValue().getIntraCluster().keySet()) {
                        String label = pair.getLeft() + "&" + pair.getRight();
                        outputWriter.write(label + "_min\t" + label + "_max\t" + label + "_avg\t");
                    }
                    outputWriter.newLine();
                    for (Map.Entry<Double, MetricStatistics> metricStatistics : sortedList) {
                        outputWriter.write(metricStatistics.getKey() + "\t" +
                                metricStatistics.getValue().getMinValue() + "\t" +
                                metricStatistics.getValue().getMaxValue() + "\t" +
                                metricStatistics.getValue().getAvgValue() + "\t" +
                                data.get(entry.getKey()).get(metricStatistics.getKey()) + "\t");
                        for (Map.Entry<Pair<String, String>, MetricStatistics> intraCluster : metricStatistics.getValue().getIntraCluster().entrySet()) {
                            outputWriter.write(intraCluster.getValue().getMinValue() + "\t" +
                                    intraCluster.getValue().getMaxValue() + "\t" +
                                    intraCluster.getValue().getAvgValue() + "\t");
                        }
                        outputWriter.newLine();
                    }
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            log.error("IOException while write metric statistics", e);
        }

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

    public TaskPoolResult draw(String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        log.info("Draw...");
        try {
            GNUPlotAdapter ga = new GNUPlotAdapter();
            ga.draw(data, imgTitle, xrange, xticks, yrange, yticks, smooth);
        } catch (RuntimeException e) {
            log.error("RuntimeException while write picture", e);
        }
        return this;
    }
}
