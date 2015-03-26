package com.thesis.workflow;

import com.thesis.metric.Scale;

import java.util.Arrays;
import java.util.Map;

import java.util.stream.Collectors;

public class Context {
    private static volatile Context instance;

    public String GNUPLOT_PATH = null;
    public String IMG_FOLDER = null;
    public Boolean PARALLEL = null;
    public Scale SCALE = null;

    private Context() {
    }

    public static Context getInstance() {
        Context localInstance = instance;
        if (localInstance == null) {
            synchronized (Context.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Context();
                }
            }
        }
        return localInstance;
    }

    public void init(String gnuplotPath, String imgFolder, Boolean parallel, Scale scale) {
        this.GNUPLOT_PATH = gnuplotPath;
        this.IMG_FOLDER = imgFolder;
        this.PARALLEL = parallel;
        this.SCALE = scale;
    }

    public boolean checkContext() {
        return GNUPLOT_PATH != null && IMG_FOLDER != null && PARALLEL != null && SCALE != null;
    }
}
