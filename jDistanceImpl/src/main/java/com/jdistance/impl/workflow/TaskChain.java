package com.jdistance.impl.workflow;

import com.jdistance.impl.adapter.gnuplot.ArrayUtils;
import com.jdistance.impl.adapter.gnuplot.GNUPlotAdapter;
import com.jdistance.impl.adapter.gnuplot.Plot;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.Distance;
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

    String name = null;
    List<Task> tasks = null;

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

    public void setName(String name) {
        this.name = name;
    }

    public TaskChain addTasks(List<Task> tasks) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.addAll(tasks);

        return this;
    }

    public TaskChain addTask(Task task) {
        if (this.tasks == null) {
            this.tasks = new ArrayList<>();
        }
        this.tasks.add(task);

        return this;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public TaskChain execute() {
        if (!Context.getInstance().checkContext()) {
            throw new RuntimeException("context not initialized!");
        }

        Date start = new Date();
        log.info("Start task chain {}", name);
        Stream<Task> stream = Context.getInstance().PARALLEL ? tasks.parallelStream() : tasks.stream();

        stream.forEach(task -> {
            Date startTask = new Date();
            log.info("Task start: {}", task.getName());
            task.execute();
            Date finishTask = new Date();
            long diffTask = finishTask.getTime() - startTask.getTime();
            log.info("Task done: {}. Time: {} ", task.getName(), diffTask);
        });
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("Task chain done. Time: {}", diff);
        return this;
    }

    public TaskChain draw() {
        write(name);
        return draw(name);
    }

    public TaskChain write() {
        return write(name);
    }

    public TaskChain draw(String imgTitle) {
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<Plot> plots = new ArrayList<>();
        tasks.forEach(task -> {
            Distance distance = task.getDistance();
            Map<Double, Double> points = task.getResults();

            String plotTitle = distance.getName();
            List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new Plot(plotTitle, color.next(), plotPointsSet));
        });

        GNUPlotAdapter ga = new GNUPlotAdapter(Context.getInstance().GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Context.getInstance().IMG_FOLDER + File.separator + imgTitle + ".png");

        return this;
    }

    public TaskChain write(String filename) {
        String path = Context.getInstance().IMG_FOLDER + File.separator + filename + ".txt";
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path))) {
            for (Task task : tasks) {
                Distance distance = task.getDistance();
                outputWriter.write(distance.getName() + "\t");
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
