package com.jdistance.spark.workflow;

import com.jdistance.workflow.AbstractTaskPoolResult;

import java.util.*;

public class SparkTaskPoolResult extends AbstractTaskPoolResult {
    public SparkTaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
        super(name, taskNames, data);
    }

    public SparkTaskPoolResult writeData(String filePath) {
        super.writeData(filePath);
        return this;
    }
}
