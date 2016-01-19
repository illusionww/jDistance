package com.jdistance.impl.workflow;

import com.jdistance.impl.adapter.gnuplot.ArrayUtils;
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
        log.info("START task CHAIN {}", name);
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
        log.info("Task CHAIN DONE. Time: {}", diff);
        return this;
    }

    public TaskChain draw() {
        write(name);
        return draw(name);
    }

    public TaskChain draw(String imgTitle) {
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<PlotDTO> plots = new ArrayList<>();
        tasks.forEach(task -> {
            MetricWrapper metricWrapper = task.getMetricWrapper();
            Map<Double, Double> points = task.getResults();

            String plotTitle = metricWrapper.getName();
            List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new PlotDTO(plotTitle, color.next(), plotPointsSet));
        });

        GNUPlotAdapter ga = new GNUPlotAdapter(ContextProvider.getInstance().getContext().getGnuplotPath());
        ga.drawData(imgTitle, plots, ContextProvider.getInstance().getContext().getImgFolder() + File.separator + imgTitle.replaceAll("^[\\w ]+", "") + ".png");

        return this;
    }

    public TaskChain write(String filename) {
        String path = ContextProvider.getInstance().getContext().getImgFolder() + File.separator + filename + ".txt";
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path))) {
            for (Task task : tasks) {
                MetricWrapper metricWrapper = task.getMetricWrapper();
                outputWriter.write(metricWrapper.getName() + "\t");
            }
            outputWriter.newLine();
            Set<Double> points = new TreeMap<>(tasks.get(0).getResults()).keySet();
            for (Double key : points) {
                outputWriter.write(key + "\t");
                for (Task task : tasks) {
                    outputWriter.write(task.getResults().get(key) + "\t");
                }
                outputWriter.newLine();
            }

        } catch (IOException e) {
            System.err.println("IOException while write results");
        }

        return this;
    }

    public Map<Task, Map<Double, Double>> getData() {
        return tasks.stream().collect(Collectors.toMap(task -> task, Task::getResults));
    }
}
