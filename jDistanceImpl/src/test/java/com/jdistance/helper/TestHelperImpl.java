package com.jdistance.helper;

import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.task.Task;
import com.jdistance.metric.MetricWrapper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TestHelperImpl {
    public static final String GNUPLOT_PATH = "C:\\cygwin64\\bin\\gnuplot.exe";
    public static final String TEST_FOLDER = "./temp_test";

    public static void initTestContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = GNUPLOT_PATH;
        context.IMG_FOLDER = TEST_FOLDER;
        context.COMPETITION_FOLDER = TEST_FOLDER;
        context.PARALLEL = false;

        File testFolder = new File(TEST_FOLDER);
        if (testFolder.exists()) {
            for (File file : testFolder.listFiles()) {
                file.delete();
            }
        }
        testFolder.mkdirs();
    }

    public static Map<MetricWrapper, Map<Double, Double>> toDistanceMap(Map<Task, Map<Double, Double>> map) {
        Map<MetricWrapper, Map<Double, Double>> result = new HashMap<>();
        map.entrySet().forEach(entry -> {
            result.put(entry.getKey().getMetricWrapper(), entry.getValue());
        });
        return result;
    }

    public static boolean equalDoubleStrict(double a, double b) {
        return Math.abs(a - b) < 0.0000001;
    }
}
