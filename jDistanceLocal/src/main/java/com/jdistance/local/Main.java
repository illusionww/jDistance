package com.jdistance.local;

import com.jdistance.distance.Kernel;
import com.jdistance.distance.KernelWrapper;
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
        Context.fill(false, true, true, "./results/data", "./results/img");
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
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.3, 0.1));
        new TaskPool()
                .buildSimilarTasks(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Arrays.asList(
                        new KernelWrapper(Kernel.SP_CT_H),
                        new KernelWrapper(Kernel.SP_CT_K),
                        new KernelWrapper(Kernel.SP_CT_NEW_H)
                ), graphs, 50)
                .execute()
                .drawUnique("[0:1]", "0.2");

    }
}

