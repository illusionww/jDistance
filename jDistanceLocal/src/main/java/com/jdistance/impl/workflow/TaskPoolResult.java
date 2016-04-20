package com.jdistance.impl.workflow;

import com.jdistance.gridsearch.statistics.ClustersMeasureStatistics;
import com.jdistance.impl.adapter.GNUPlotAdapter;
import com.panayotis.gnuplot.style.Smooth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TaskPoolResult {
    private static final Logger log = LoggerFactory.getLogger(TaskPoolResult.class);

    private String name;
    private List<String> taskNames;
    private Map<String, Map<Double, Double>> data;
    private Map<String, Map<Double, ClustersMeasureStatistics>> metricStatistics;

    TaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data, Map<String, Map<Double, ClustersMeasureStatistics>> metricStatistics) {
        this.name = name;
        this.taskNames = taskNames;
        this.data = data;
        this.metricStatistics = metricStatistics;
    }

    public String getName() {
        return name;
    }

    public Map<String, Map<Double, Double>> getData() {
        return data;
    }

    public TaskPoolResult addMeasuresStatisticsToData() {
        log.info("Add metrics statistics to data...");
        if (Context.getInstance().isCollectMetricStatistics()) {
            List<String> newTaskNames = new ArrayList<>();
            for (String taskName : taskNames) {
                data.put(taskName + "_min", metricStatistics.get(taskName).entrySet().stream()
                        .filter(e -> e.getValue().getMinValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getMinValue())));
                data.put(taskName + "_max", metricStatistics.get(taskName).entrySet().stream()
                        .filter(e -> e.getValue().getMaxValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getMaxValue())));
                data.put(taskName + "_avg", metricStatistics.get(taskName).entrySet().stream()
                        .filter(e -> e.getValue().getAvgValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAvgValue())));
                data.put(taskName + "_diagavg", metricStatistics.get(taskName).entrySet().stream()
                        .filter(e -> e.getValue().getAvgDiagValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAvgDiagValue())));
                newTaskNames.addAll(Arrays.asList(taskName + "_min", taskName + "_max", taskName + "_avg", taskName + "_diagavg"));
            }
            taskNames.addAll(newTaskNames);
        }
        return this;
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

            outputWriter.write("\t");
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

    class DefaultHashMap<K, V> extends HashMap<K, V> {
        private V defaultValue;

        public DefaultHashMap(V defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public V get(Object k) {
            return containsKey(k) ? super.get(k) : defaultValue;
        }
    }
}
