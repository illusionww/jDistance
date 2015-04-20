package com.thesis.workflow;

import com.panayotis.gnuplot.utils.FileUtils;
import com.thesis.metric.Scale;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import java.util.stream.Collectors;

public class Context {
    private static volatile Context instance;

    public String GNUPLOT_PATH = null;
    public String IMG_FOLDER = null;
    public String CACHE_FOLDER = null;
    public Boolean PARALLEL = null;
    public Boolean USE_CACHE = null;

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

    public boolean checkContext() {
        GNUPLOT_PATH = isExist(GNUPLOT_PATH) ? GNUPLOT_PATH : FileUtils.findPathExec();

        if (GNUPLOT_PATH == null || IMG_FOLDER == null || CACHE_FOLDER == null) {
            throw new RuntimeException("Context not filled properly");
        }

        if (!isExist(GNUPLOT_PATH)) {
            throw new RuntimeException("Gnuplot not found");
        }

        if (!isExist(IMG_FOLDER) && !new File(IMG_FOLDER).mkdirs()) {
            throw new RuntimeException("Folder " + new File(IMG_FOLDER).getAbsolutePath() + " is not exist");
        }

        if (!isExist(CACHE_FOLDER) && !new File(CACHE_FOLDER).mkdirs()) {
            throw new RuntimeException("Folder " + new File(CACHE_FOLDER).getAbsolutePath() + " is not exist");
        }

        return PARALLEL != null && USE_CACHE != null;
    }

    private static boolean isExist(String path) {
        return path != null && new File(path).exists();
    }
}
