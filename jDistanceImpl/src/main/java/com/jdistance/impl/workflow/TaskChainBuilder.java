package com.jdistance.impl.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.graph.generator.GeneratorPropertiesDTO;
import com.jdistance.impl.workflow.gridsearch.GridSearch;
import com.jdistance.impl.workflow.gridsearch.clusterer.MinSpanningTreeGridSearch;
import com.jdistance.impl.workflow.gridsearch.clusterer.WardGridSearch;
import com.jdistance.impl.workflow.gridsearch.nolearning.DiffusionGridSearch;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;

import java.util.ArrayList;
import java.util.List;

public class TaskChainBuilder {
    private String name;
    private GraphBundle graphs;
    private List<MetricWrapper> metricWrappers;
    private Integer pointsCount;
    private List<Task> tasks;

    public TaskChainBuilder(String name, List<MetricWrapper> metricWrappers, Integer pointsCount) {
        this(metricWrappers, pointsCount);
        this.name = name;
    }

    public TaskChainBuilder(List<MetricWrapper> metricWrappers, Integer pointsCount) {
        this.metricWrappers = metricWrappers;
        this.pointsCount = pointsCount;
        this.tasks = new ArrayList<>();
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskChainBuilder generateGraphs(int graphCount, int nodesCount, int clustersCount, double pIn, double pOut) {
        GeneratorPropertiesDTO properties = new GeneratorPropertiesDTO(graphCount, nodesCount, clustersCount, pIn, pOut);
        graphs = GnPInPOutGraphGenerator.getInstance().generate(properties);
        return this;
    }

    public TaskChainBuilder setGraphs(GraphBundle graphs) {
        this.graphs = graphs;
        return this;
    }

    public TaskChainBuilder setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
        return this;
    }

    public TaskChainBuilder addTask(Task task) {
        tasks.add(task);
        return this;
    }

    public TaskChainBuilder generateMinSpanningTreeTasks() {
        if (name == null) name = generateName("MinSpanningTree", graphs.getProperties());
        GridSearch gridSearch = new MinSpanningTreeGridSearch(graphs, graphs.getProperties().getClustersCount());
        tasks.addAll(generateDefaultTasks(gridSearch, metricWrappers, pointsCount));
        return this;
    }

    public TaskChainBuilder generateWardTasks() {
        if (name == null) name = generateName("Ward", graphs.getProperties());
        GridSearch gridSearch = new WardGridSearch(graphs, graphs.getProperties().getClustersCount());
        tasks.addAll(generateDefaultTasks(gridSearch, metricWrappers, pointsCount));
        return this;
    }

    public TaskChainBuilder generateDiffusionTasks() {
        if (name == null) name = generateName("Diffusion", graphs.getProperties());
        GridSearch gridSearch = new DiffusionGridSearch(graphs);
        tasks.addAll(generateDefaultTasks(gridSearch, metricWrappers, pointsCount));
        return this;
    }

    public TaskChain build() {
        if (name == null) {
            throw new RuntimeException("Name shouldn't be null!");
        }
        TaskChain chain = new TaskChain(name);
        chain.addTasks(tasks);
        return chain;
    }

    public static List<Task> generateDefaultTasks(GridSearch gridSearch, List<MetricWrapper> metricWrappers, int pointsCount) {
        List<Task> tasks = new ArrayList<>();
        metricWrappers.forEach(metricWrapper -> {
            GridSearch gridSearchClone = gridSearch.clone();
            tasks.add(new DefaultTask(gridSearchClone, metricWrapper, pointsCount));
        });
        return tasks;
    }

    private String generateName(String checkerName, GeneratorPropertiesDTO properties) {
        return checkerName + ": " +
                properties.getNodesCount() + " nodes, " +
                properties.getClustersCount() + " clusters, " +
                "pIn=" + properties.getP_in() + ", " +
                "pOut=" + properties.getP_out() + ", " +
                properties.getGraphsCount() + " graphs";
    }
}