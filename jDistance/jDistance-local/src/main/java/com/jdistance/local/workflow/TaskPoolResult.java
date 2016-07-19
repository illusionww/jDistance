package com.jdistance.local.workflow;

import com.jdistance.local.adapter.GNUPlotAdapter;
import com.jdistance.workflow.AbstractTaskPoolResult;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class TaskPoolResult extends AbstractTaskPoolResult {
    private static final Logger log = LoggerFactory.getLogger(TaskPoolResult.class);

    public TaskPoolResult(String name, Map<String, Map<Double, Pair<Double, Double>>> data) {
        super(name, data);
    }

    public TaskPoolResult writeData() {
        String filePath = Context.getInstance().buildOutputDataFullName(name, "csv");
        log.info("Write data to: {}", filePath);
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(filePath))) {
            writeData(outputWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public TaskPoolResult draw() {
        String filePath = Context.getInstance().buildImgFullName(name, "png");
        log.info("Draw line graph to: {}", filePath);
        try {
            new GNUPlotAdapter().draw(filePath, data);
        } catch (RuntimeException e) {
            log.error("RuntimeException while write picture", e);
        }
        return this;
    }
}
