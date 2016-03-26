package com.jdistance.impl.workflow;

import com.jdistance.impl.adapter.gnuplot.GNUPlotAdapter;
import com.jdistance.impl.adapter.gnuplot.PlotDTO;
import com.jdistance.impl.workflow.context.ContextProvider;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;
import com.panayotis.gnuplot.style.Smooth;
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
        log.info("START TASK_CHAIN \"{}\"", name);

        Stream<Task> stream = ContextProvider.getContext().getParallelTasks() ? tasks.parallelStream() : tasks.stream();
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
        Iterator<PlotColor> color = Arrays.asList(GNUPlotAdapter.colors).iterator();

        List<PlotDTO> plots = new ArrayList<>();
        tasks.forEach(task -> {
            String plotTitle = task.getMetricWrapper() != null ? task.getMetricWrapper().getName() : task.getName();
            Map<Double, Double> points = task.getResult();
            List<Point<Double>> plotPoints = GNUPlotAdapter.mapToPoints(points);
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new PlotDTO(plotTitle, color.next(), plotPointsSet));
        });

        GNUPlotAdapter ga = new GNUPlotAdapter(ContextProvider.getContext().getGnuplotPath());
        ga.drawData(plots, buildFullImgNameByImgTitle(imgTitle, "png"), buildFullImgNameByImgTitle(imgTitle, "gnu"), yrange, yticks, smooth);

        return this;
    }

    public TaskChain write() {
        write(name);
        return this;
    }

    public TaskChain write(String filename) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(buildFullDataNameByImgTitle(filename, "csv")))) {
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
//            writeMaxMinValues(outputWriter);
        } catch (IOException e) {
            System.err.println("IOException while write results");
        }

        return this;
    }

    public void writeMaxMinValues(BufferedWriter outputWriter) throws IOException {
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
    }

    public Map<Task, Map<Double, Double>> getData() {
        return tasks.stream().collect(Collectors.toMap(task -> task, Task::getResult));
    }

    private static String buildFullDataNameByImgTitle(String imgTitle, String extension) {
        return ContextProvider.getContext().getCalculationsResultFolder() + File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") + "." + extension;
    }

    private static String buildFullImgNameByImgTitle(String imgTitle, String extension) {
        return ContextProvider.getContext().getImgFolder() + File.separator +
                imgTitle.replaceAll("[^\\w\\-\\.,= ]+", "_") + "." + extension;
    }

}
