package com.jdistance.spark.workflow;

import org.apache.spark.api.java.JavaPairRDD;

import java.util.*;

public class TaskPoolResult {

    private String name;
    private List<String> taskNames;
    private Map<String, JavaPairRDD<Double, Double>> data;

    public TaskPoolResult(String name, List<String> taskNames, Map<String, JavaPairRDD<Double, Double>> data) {
        this.name = name;
        this.taskNames = taskNames;
        this.data = data;
    }

    public String getName() {
        return name;
    }
}
