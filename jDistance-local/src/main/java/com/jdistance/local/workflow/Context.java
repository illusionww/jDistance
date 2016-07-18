package com.jdistance.local.workflow;

import java.io.File;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class Context {
    private static Context instance;

    private Boolean isParallelTasks;
    private Boolean isParallelGrid;

    private String outputDataFolder;
    private String imgFolder;

    public static void fill(Boolean parallelTasks, Boolean parallelGrid, String outputDataFolder, String imgFolder) {
        instance = new Context();
        instance.isParallelTasks = parallelTasks;
        instance.isParallelGrid = parallelGrid;
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

    public String buildOutputDataFullName(String imgTitle, String extension) {
        return outputDataFolder + File.separator +
                imgTitle.replaceAll("[:\\\\/*?|<>]", "_") + "." + extension;
    }

    public String buildImgFullName(String subfolder, String imgTitle, String extension) {
        String subfolderPath = imgFolder + File.separator + subfolder.replaceAll("[:\\\\/*?|<>]", "_");
        File outputDataFolderFile = new File(subfolderPath);
        if (!outputDataFolderFile.exists() && !outputDataFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + outputDataFolderFile.getAbsolutePath() + " is not exist");
        }
        return subfolderPath + File.separator +
                (ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + " " + imgTitle).replaceAll("[:\\\\/*?|<>]", "_") +
                "." + extension;
    }

    public String buildImgFullName(String imgTitle, String extension) {
        return imgFolder + File.separator +
                imgTitle.replaceAll("[:\\\\/*?|<>]", "_") + "." + extension;
    }
}