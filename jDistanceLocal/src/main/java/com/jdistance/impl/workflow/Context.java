package com.jdistance.impl.workflow;

import java.io.File;

public class Context {
    private static Context instance;

    private Boolean isParallelTasks;
    private Boolean isParallelGrid;
    private Boolean calcMetricStatistics;

    private String outputDataFolder;
    private String imgFolder;

    public static void fill(Boolean parallelTasks, Boolean parallelGrid, Boolean calcMetricStatistics, String outputDataFolder, String imgFolder) {
        instance = new Context();
        instance.isParallelTasks = parallelTasks;
        instance.isParallelGrid = parallelGrid;
        instance.calcMetricStatistics = calcMetricStatistics;
        instance.outputDataFolder = outputDataFolder;
        instance.imgFolder = imgFolder;

        File outputDataFolderFile = new File(outputDataFolder);
        if (!outputDataFolderFile.exists() && !outputDataFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + outputDataFolderFile.getAbsolutePath() + " is not exist");
        }
        File imgFolderFile = new File(imgFolder);
        if (!imgFolderFile.exists() && !imgFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + imgFolderFile.getAbsolutePath() + " is not exist");
        }
    }

    public static Context getInstance() {
        if (instance == null) {
            throw new RuntimeException("Context is not filled!");
        }
        return instance;
    }

    public Boolean isParallelTasks() {
        return isParallelTasks;
    }

    public Boolean isParallelGrid() {
        return isParallelGrid;
    }

    public Boolean getCalcMetricStatistics() {
        return calcMetricStatistics;
    }

    public String buildOutputDataFullName(String imgTitle, String extension) {
        return outputDataFolder + File.separator +
                imgTitle.replaceAll("[:\\\\/*?|<>]", "_") + "." + extension;
    }

    public String buildImgFullName(String imgTitle, String extension) {
        return imgFolder + File.separator +
                imgTitle.replaceAll("[:\\\\/*?|<>]", "_") + "." + extension;
    }
}