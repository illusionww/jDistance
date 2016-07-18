package com.jdistance.local.workflow;

import java.io.File;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class Context {
    private static Context instance;

    private Boolean isParallelTasks;
    private String outputDataFolder;
    private String outputImgFolder;

    public static void fill(Boolean parallelTasks, String outputDataFolder, String outputImgFolder) {
        instance = new Context();
        instance.isParallelTasks = parallelTasks;
        instance.outputImgFolder = outputImgFolder;
        instance.outputDataFolder = outputDataFolder;

        File outputDataFolderFile = new File(outputDataFolder);
        if (!outputDataFolderFile.exists() && !outputDataFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + outputDataFolderFile.getAbsolutePath() + " can't be created");
        }
        File imgFolderFile = new File(outputImgFolder);
        if (!imgFolderFile.exists() && !imgFolderFile.mkdirs()) {
            throw new RuntimeException("Folder " + imgFolderFile.getAbsolutePath() + " can't be created");
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

    public String buildOutputDataFullName(String imgTitle, String extension) {
        return outputDataFolder + File.separator + (ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + " " + imgTitle).replaceAll("[:\\\\/*?|<>]", "_") + "." + extension;
    }

    public String buildImgFullName(String imgTitle, String extension) {
        return outputImgFolder + File.separator + (ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + " " + imgTitle).replaceAll("[:\\\\/*?|<>]", "_") +
                "." + extension;
    }
}