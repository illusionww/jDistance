package com.jdistance.spark.workflow;

import com.jdistance.workflow.AbstractTaskPoolResult;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

public class TaskPoolResult extends AbstractTaskPoolResult {
    public TaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
        super(name, taskNames, data);
    }

    public TaskPoolResult writeData(String filePath) {
        StringWriter stringWriter = new StringWriter();
        try {
            super.writeData(stringWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = stringWriter.toString();
        Context.getInstance().getSparkContext()
                .parallelize(Collections.singletonList(data))
                .saveAsTextFile(filePath);
        return this;
    }
}
