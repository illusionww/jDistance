package com.jdistance.spark.workflow;

import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class Context {
    private static Context instance;

    private JavaSparkContext sparkContext;
    private String outputDataFolder;


    public static void fill(JavaSparkContext sparkContext, String outputDataFolder) {
        instance = new Context();
        instance.sparkContext = sparkContext;
        instance.outputDataFolder = outputDataFolder;
    }

    public static Context getInstance() {
        if (instance == null) {
            throw new RuntimeException("Context is not filled!");
        }
        return instance;
    }

    public JavaSparkContext getSparkContext() {
        return sparkContext;
    }

    public String buildDataFullName(String dataTitle) {
        return outputDataFolder + File.separator +
                (ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()) + " " + dataTitle).replaceAll("[:\\\\/*?|<>]", "_");
    }
}
