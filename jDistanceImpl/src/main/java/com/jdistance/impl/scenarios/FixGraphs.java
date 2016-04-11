package com.jdistance.impl.scenarios;

import com.jdistance.impl.workflow.TaskChainBuilder;

public class FixGraphs {
    private static void fixGraphs() {
        fixGraph("polbooks_ward", "results/data/polbooks_ward.csv", "[0.39:1]", "0.2");
    }

    private static void fixGraph(String name, String path, String yrange, String yticks) {
        new TaskChainBuilder(name, null, null).importDataFromFile(path).build()
                .drawUniqueAndBezier(yrange, yticks);
    }
}
