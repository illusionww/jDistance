package com.thesis.workflow;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;
import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.metric.Distance;
import com.thesis.utils.ArrayUtils;
import com.thesis.workflow.task.Task;

import java.io.File;
import java.util.*;

public class TaskChain {
    List<Task> tasks;

    public TaskChain() {
    }

    public TaskChain(Task task) {
        tasks = new ArrayList<>();
        tasks.add(task);
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
        tasks.forEach(Task::execute);

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

        GNUPlotAdapter ga = new GNUPlotAdapter(Environment.GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Environment.IMG_FOLDER + File.separator + imgTitle + ".png");

        return this;
    }
}