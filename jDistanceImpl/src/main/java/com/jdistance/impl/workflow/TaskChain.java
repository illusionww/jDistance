package com.jdistance.impl.workflow;

import com.jdistance.impl.adapter.gnuplot.GNUPlotAdapter;
import com.jdistance.impl.adapter.gnuplot.PlotDTO;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static String buildPNGFullNameByImgTitle(String imgTitle) {
        return ContextProvider.getInstance().getContext().getImgFolder() +
                File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") +
                ".png";
    }

    public static String buildTXTFullNameByImgTitle(String imgTitle) {
        return ContextProvider.getInstance().getContext().getImgFolder() +
                File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") +
                ".txt";
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
        Stream<Task> stream = ContextProvider.getInstance().getContext().getParallel() ? tasks.parallelStream() : tasks.stream();

        stream.forEach(task -> {
            Date startTask = new Date();
            log.info("Task START: {}", task.getName());
            task.execute();
            Date finishTask = new Date();
            long diffTask = finishTask.getTime() - startTask.getTime();
            log.info("Task DONE: {}.\n\tTime: {} ", task.getName(), diffTask);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("TASK_CHAIN DONE. Time: {}", diff);
        log.info("---------------------------------------------------------------------------------------------", diff);
        return this;
    }

    public TaskChain draw() {
        draw(name);
        return this;
    }

    public TaskChain draw(String imgTitle) {
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<PlotDTO> plots = new ArrayList<>();
        tasks.forEach(task -> {
            MetricWrapper metricWrapper = task.getMetricWrapper();
            Map<Double, Double> points = task.getResult();

            String plotTitle = metricWrapper.getName();
            List<Point<Double>> plotPoints = GNUPlotAdapter.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new PlotDTO(plotTitle, color.next(), plotPointsSet));
        });

        GNUPlotAdapter ga = new GNUPlotAdapter(ContextProvider.getInstance().getContext().getGnuplotPath());
        ga.drawData(imgTitle, plots, buildPNGFullNameByImgTitle(imgTitle));

        return this;
    }

    public TaskChain write() {
        write(name);
        return this;
    }

    public TaskChain write(String filename) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(buildTXTFullNameByImgTitle(filename)))) {
            outputWriter.write("\t");
            for (Task task : tasks) {
                MetricWrapper metricWrapper = task.getMetricWrapper();
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
            outputWriter.write("MIN_param\t");
            for (Task task : tasks) {
                outputWriter.write(task.getMinResult().getKey() + "\t");
            }
            outputWriter.newLine();

            outputWriter.write("MIN_value\t");
            for (Task task : tasks) {
                outputWriter.write(task.getMinResult().getValue() + "\t");
            }
            outputWriter.newLine();

            outputWriter.write("MAX_param\t");
            for (Task task : tasks) {
                outputWriter.write(task.getMaxResult().getKey() + "\t");
            }
            outputWriter.newLine();

            outputWriter.write("MAX_value\t");
            for (Task task : tasks) {
                outputWriter.write(task.getMaxResult().getValue() + "\t");
            }
            outputWriter.newLine();
        } catch (IOException e) {
            System.err.println("IOException while write results");
        }

        return this;
    }

    public Map<Task, Map<Double, Double>> getData() {
        return tasks.stream().collect(Collectors.toMap(task -> task, Task::getResult));
    }
}
