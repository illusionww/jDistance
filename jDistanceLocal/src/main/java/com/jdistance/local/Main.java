package com.jdistance.local;

import com.jdistance.distance.Distance;
import com.jdistance.distance.DistanceWrapper;
import com.jdistance.distance.Kernel;
import com.jdistance.distance.KernelWrapper;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.local.competitions.RejectCurve;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.TaskPool;
import com.jdistance.local.workflow.TaskPoolResult;
import jeigen.DenseMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Context.fill(false, true, true, "./results/data", "./results/img");

        if (args.length != 1) {
            throw new RuntimeException("There is no task param!");
        }

        String methodName = args[0];

        Class clazz = Class.forName("com.jdistance.local.Main");
        Method method = clazz.getMethod(methodName);
        method.setAccessible(true);
        log.info("Run method \"" + methodName + "\"");
        method.invoke(new Main());
        log.info("Done method \"" + methodName + "\"");
    }

    public void saa() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, 150, 2, 0.3, 0.1));
        new TaskPool()
                .buildSimilarTasks(new Ward(graphs.getProperties().getClustersCount()), Scorer.RI, Kernel.getAllH(), graphs, 50)
                .execute()
                .drawUnique("[0:1]", "0.2");

    }
}

