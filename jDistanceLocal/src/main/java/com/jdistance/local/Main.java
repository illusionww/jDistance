package com.jdistance.local;

import com.jdistance.measure.Kernel;
import com.jdistance.measure.KernelWrapper;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.TaskPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Context.fill(false, true, "./results/data", "./results/img");
        if (args.length == 1) {
            String methodName = args[0];
            Class<?> clazz = Class.<Main>forName("com.jdistance.local.Main");
            Method method = clazz.getMethod(methodName);
            method.setAccessible(true);
            log.info("Run job \"" + methodName + "\"");
            method.invoke(new Main());
            log.info("Done job \"" + methodName + "\"");
        } else {
            throw new RuntimeException("There is no task param!");
        }
    }

    public void saa() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, new int[]{
                100, 75, 75, 50, 25, 10, 5 // sum = 340
        }, new double[][] {
                {0.3, 0.2, 0.1, 0.13, 0.02, 0.2, 0.1},
                {0.0, 0.2, 0.1, 0.13, 0.02, 0.2, 0.1},
                {0.0, 0.0, 0.1, 0.13, 0.02, 0.2, 0.1},
                {0.0, 0.0, 0.0, 0.13, 0.02, 0.2, 0.1},
                {0.0, 0.0, 0.0, 0.00, 0.10, 0.2, 0.1},
                {0.0, 0.0, 0.0, 0.00, 0.00, 0.12, 0.1},
                {0.0, 0.0, 0.0, 0.00, 0.00, 0.0, 0.14}
        }));
        new TaskPool()
                .buildSimilarTasks(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Arrays.asList(
                        new KernelWrapper(Kernel.LOG_COMM_H)
                ), graphs, 35)
                .execute()
                .drawUnique("[0:1]", "0.2");
    }
}

