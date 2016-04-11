package com.jdistance.impl.workflow;

import com.jdistance.impl.adapter.gnuplot.GNUPlotAdapter;
import com.jdistance.impl.workflow.gridsearch.MetricStatisticsDTO;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.AbstractDistanceWrapper;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.style.Smooth;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jdistance.impl.workflow.context.ContextProvider.getContext;

public class TaskChain {
    private static final Logger log = LoggerFactory.getLogger(TaskChain.class);

    private String name;
    private List<Task> tasks;

    public TaskChain(String name, Task... tasks) {
        this.name = name;
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
    }

    public TaskChain(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = new ArrayList<>(tasks);
    }

    public String getName() {
        return name;
    }

    public TaskChain addTasks(List<Task> tasks) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.addAll(tasks);

        return this;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public TaskChain execute() {
        Date start = new Date();
        log.info("START TASK_CHAIN \"{}\"", name);

        Stream<Task> stream = getContext().getParallelTasks() ? tasks.parallelStream() : tasks.stream();
        stream.forEach(task -> {
            Date startTask = new Date();
            log.info("Task START: {}", task.getName());
            task.execute();
            Date finishTask = new Date();
            long diffTask = finishTask.getTime() - startTask.getTime();
            log.info("Task DONE: {}. Time: {} ", task.getName(), diffTask);
        });

        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("TASK_CHAIN DONE. Time: {}", diff);
        log.info("----------------------------------------------------------------------------------------------------", diff);
        return this;
    }

    public TaskChain drawUniqueAndBezier(String yrange, String yticks) {
        drawUnique(yrange, yticks);
        drawBezier(yrange, yticks);
        return this;
    }

    public TaskChain drawUnique(String yrange, String yticks) {
        draw(name + ".UNIQUE", yrange, yticks, Smooth.UNIQUE);
        return this;
    }

    public TaskChain drawBezier(String yrange, String yticks) {
        draw(name + ".BEZIER", yrange, yticks, Smooth.BEZIER);
        return this;
    }

    public TaskChain draw(String imgTitle, String yrange, String yticks, Smooth smooth) {
        draw(imgTitle, "[0:1]", "0.2", yrange, yticks, smooth);
        return this;
    }


    public TaskChain draw(String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        GNUPlotAdapter ga = new GNUPlotAdapter(getContext().getGnuplotPath());
        ga.draw(this, imgTitle, xrange, xticks, yrange, yticks, smooth);
        return this;
    }

    public TaskChain writeData() {
        writeData(name);
        return this;
    }

    public TaskChain writeData(String filename) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(getContext().buildDataFullName(filename, "csv")))) {
            outputWriter.write("\t");
            for (Task task : tasks) {
                AbstractDistanceWrapper metricWrapper = task.getMetricWrapper();
                outputWriter.write(metricWrapper.getName() + "\t");
            }
            outputWriter.newLine();
            Set<Double> points = new TreeSet<>();
            tasks.forEach(task -> points.addAll(task.getResult().keySet()));
            for (Double key : points) {
                outputWriter.write(key + "\t");
                for (Task task : tasks) {
                    outputWriter.write(task.getResult().get(key) + "\t");
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("IOException while writeData results");
        }

        return this;
    }

    public TaskChain writeStatistics() {
        writeStatistics(name);
        return this;
    }

    public TaskChain writeStatistics(String filename) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(getContext().buildDataFullName(filename + "_metric-statistics", "csv")))) {
                for (Task task : tasks) {
                outputWriter.write("Metric " + task.getName() + "\n");
                for (Map<Double, MetricStatisticsDTO> statisticsForGraph : task.getMetricStatistics().values()) {
                    List<Map.Entry<Double, MetricStatisticsDTO>> sortedList = new ArrayList<>(statisticsForGraph.entrySet());
                    Collections.sort(sortedList, Comparator.comparingDouble(Map.Entry::getKey));

                    outputWriter.write("param\tmin\tmax\tavg\tscore\t");
                    for (Pair<String, String> pair : sortedList.get(0).getValue().getIntraCluster().keySet()) {
                        String label = pair.getLeft() + "&" + pair.getRight();
                        outputWriter.write(label + "_min\t" + label + "_max\t" + label + "_avg\t");
                    }
                    outputWriter.newLine();
                    for (Map.Entry<Double, MetricStatisticsDTO> metricStatistics : sortedList) {
                        outputWriter.write(metricStatistics.getKey() + "\t" +
                                metricStatistics.getValue().getMinValue() + "\t" +
                                metricStatistics.getValue().getMaxValue() + "\t" +
                                metricStatistics.getValue().getAvgValue() + "\t" +
                                task.getResult().get(metricStatistics.getKey()) + "\t");
                        for (Map.Entry<Pair<String, String>, MetricStatisticsDTO> intraCluster : metricStatistics.getValue().getIntraCluster().entrySet()) {
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
            System.err.println("IOException while writeData results");
        }
        return this;
    }

    public Map<Task, Map<Double, Double>> getData() {
        return tasks.stream().collect(Collectors.toMap(task -> task, Task::getResult));
    }


}
