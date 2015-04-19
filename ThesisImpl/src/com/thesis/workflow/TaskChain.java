package com.thesis.workflow;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;
import com.thesis.adapter.gnuplot.ArrayUtils;
import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.metric.Distance;
import com.thesis.workflow.task.Task;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;
import org.perf4j.aop.Profiled;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskChain {
    private static final Logger log = LoggerFactory.getLogger(TaskChain.class);

    String name = null;
    List<Task> tasks = null;

    public TaskChain(String name) {
        this.name = name;
    }

    public TaskChain(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
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

    @Profiled(tag = "TaskChain")
    public TaskChain execute() {
        if (!Context.getInstance().checkContext()) {
            throw new RuntimeException("context not initialized!");
        }

        Date start = new Date();
        log.info("Start task chain {}", name);
        Stream<Task> stream = Context.getInstance().PARALLEL ? tasks.parallelStream() : tasks.stream();
        stream.forEach(Task::execute);
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("Task chain done. Time: " + diff);
        return this;
    }

    public TaskChain draw() {
        return draw(name);
    }

    public TaskChain draw(String imgTitle) {
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<Plot> plots = new ArrayList<>();
        tasks.forEach(task -> {
            Distance distance = task.getDistance();
            Map<Double, Double> points = task.getResults();

            String plotTitle = distance.getShortName();
            List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new Plot(plotTitle, color.next(), plotPointsSet));
        });

        GNUPlotAdapter ga = new GNUPlotAdapter(Context.getInstance().GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Context.getInstance().IMG_FOLDER + File.separator + imgTitle + ".png");

        return this;
    }

    public Map<Task, Map<Double, Double>> getData() {
        return tasks.stream().collect(Collectors.toMap(task -> task, Task::getResults));
    }
}
