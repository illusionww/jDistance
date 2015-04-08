package com.thesis.workflow;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;
import com.thesis.adapter.gnuplot.ArrayUtils;
import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.metric.Distance;
import com.thesis.workflow.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

public class TaskChain {
    private static final Logger log = LoggerFactory.getLogger(TaskChain.class);
    List<Task> tasks;

    public TaskChain() {
        tasks = new ArrayList<>();
    }

    public TaskChain(Task ... tasks) {
        this.tasks = Arrays.asList(tasks);
    }

    public TaskChain(List<Task> tasks) {
        this.tasks = tasks;
    }

    public TaskChain addTask(Task task) {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(task);

        return this;
    }

    public TaskChain execute() {
        if (!Context.getInstance().checkContext()) {
            throw new RuntimeException("context not initialized!");
        }

        Date start = new Date();
        log.info("Start task chain");
        Stream<Task> stream = Context.getInstance().PARALLEL ? tasks.parallelStream() : tasks.stream();
        stream.forEach(Task::execute);
        Date finish = new Date();
        long diff = finish.getTime() - start.getTime();
        log.info("Task chain done. Time: " + diff);
        return this;
    }

    public TaskChain draw(String imgTitle) {
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<Plot> plots = new ArrayList<>();
        tasks.forEach(task -> task.getResults().entrySet().forEach(entry -> {
            Distance distance = entry.getKey();
            Map<Double, Double> points = entry.getValue();

            String plotTitle = distance.getShortName();
            List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new Plot(plotTitle, color.next(), plotPointsSet));
        }));

        GNUPlotAdapter ga = new GNUPlotAdapter(Context.getInstance().GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Context.getInstance().IMG_FOLDER + File.separator + imgTitle + ".png");

        return this;
    }

    public Map<Task, Map<Distance, Map<Double, Double>>> getData() {
        Map<Task, Map<Distance, Map<Double, Double>>> result = new HashMap<>();
        tasks.forEach(task -> result.put(task, task.getResults()));
        return result;
    }
}
