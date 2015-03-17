package com.thesis.workflow;

import com.thesis.metric.Scale;

import java.util.Arrays;
import java.util.Map;

import java.util.stream.Collectors;

public class Context {
    public static final String GNUPLOT_PATH = "C:\\cygwin64\\bin\\gnuplot.exe";
    public static final String GRAPH_FOLDER = "graphs";
    public static final String IMG_FOLDER = "C:\\thesis";

    public static Map<String, Folder> folders = Arrays.asList(
            new Folder("n100pin03pout002k5"),
            new Folder("n100pin03pout01k5"),
            new Folder("n300pin03pout002k5"),
            new Folder("n300pin03pout003k5"),
            new Folder("n300pin03pout005k5"),
            new Folder("n300pin03pout01k5"),
            new Folder("n500pin03pout002k5"),
            new Folder("n500pin03pout003k5"),
            new Folder("n500pin03pout005k5"),
            new Folder("n500pin03pout01k5"),
            new Folder("n1000pin03pot01k5")
    ).stream().collect(Collectors.toMap(Folder::getFileName, item -> item));

    public static boolean PARALLEL;
    public static Scale SCALE;
}
