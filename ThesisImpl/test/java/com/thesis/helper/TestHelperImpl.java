package com.thesis.helper;

import com.thesis.metric.Distance;
import com.thesis.metric.Scale;
import com.thesis.utils.*;
import com.thesis.workflow.Context;
import com.thesis.workflow.task.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestHelperImpl {
    public static final String GNUPLOT_PATH = "C:\\cygwin64\\bin\\gnuplot.exe";
    public static final String TEST_FOLDER = "temp_test";

    public static void initTestContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = GNUPLOT_PATH;
        context.IMG_FOLDER = TEST_FOLDER;
        context.CACHE_FOLDER = TEST_FOLDER;
        context.COMPETITION_FOLDER = TEST_FOLDER;
        context.USE_CACHE = false;
        context.PARALLEL = false;

        File testFolder = new File(TEST_FOLDER);
        if (testFolder.exists()) {
            for(File file: testFolder.listFiles()) {
                file.delete();
            }
        }
    }

    public static Map<Distance, Map<Double, Double>> toDistanceMap(Map<Task, Map<Double, Double>> map) {
        Map<Distance, Map<Double, Double>> result = new HashMap<>();
        map.entrySet().forEach(entry -> {
            result.put(entry.getKey().getDistance(), entry.getValue());
        });
        return result;
    }

    public static boolean equalDoubleStrict(double a, double b) {
        return Math.abs(a - b) < 0.0000001;
    }
}
