package com.jdistance.spark.workflow;

import com.jdistance.workflow.TaskPoolResult;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.deploy.Docker;

import java.util.*;

public class SparkTaskPoolResult extends TaskPoolResult {
    public SparkTaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
        super(name, taskNames, data);
    }

    public SparkTaskPoolResult writeData(String filePath) {
        super.writeData(filePath);
        return this;
    }
}
