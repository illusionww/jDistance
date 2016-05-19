package com.jdistance.local;

import com.jdistance.distance.Distance;
import com.jdistance.distance.DistanceWrapper;
import com.jdistance.distance.Kernel;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.local.competitions.RejectCurve;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.TaskPool;
import com.jdistance.local.workflow.TaskPoolResult;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
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

    public void rejectCurvesFair() {
        rejectCurvesFairInternal(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.02));
        rejectCurvesFairInternal(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.05));
        rejectCurvesFairInternal(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.1));
        rejectCurvesFairInternal(new GeneratorPropertiesPOJO(200, 100, 2, 0.3, 0.15));
    }

    public void rejectCurvesFairInternal(GeneratorPropertiesPOJO properties) {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(properties);
        TaskPoolResult result = new TaskPool()
                .buildSimilarTasks(new Ward(2), Scorer.RATE_INDEX, Kernel.getAllK(), graphs, 47)
                .execute();
        Map<String, Map.Entry<Double, Double>> bestResults = result.getTaskNames().stream()
                .collect(Collectors.toMap(taskName -> taskName, result::getBestParam));
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(graphs.getName(), "csv")))) {
            outputWriter.write("Measure\tBestParam\tBestValue\n");
            for (Map.Entry<String, Map.Entry<Double, Double>> entry : bestResults.entrySet()) {
                outputWriter.write(entry.getKey() + "\t" + entry.getValue().getKey() + "\t" + entry.getValue().getValue() + "\n");
            }
            outputWriter.newLine();
        } catch (IOException ignored) {
        }
    }


    public void rejectCurves() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(100, 100, 2, 0.3, 0.1));

        rejectCurve(graphs, Distance.P_WALK, 0.7826);
        rejectCurve(graphs, Distance.WALK, 0.6957);
        rejectCurve(graphs, Distance.FOR, 0.9348);
        rejectCurve(graphs, Distance.LOG_FOR, 0.1957);
        rejectCurve(graphs, Distance.COMM, 0.1957);
        rejectCurve(graphs, Distance.LOG_COMM, 0.3913);
        rejectCurve(graphs, Distance.HEAT, 0.4783);
        rejectCurve(graphs, Distance.LOG_HEAT, 0.4565);
        rejectCurve(graphs, Distance.RSP, 0.9783);
        rejectCurve(graphs, Distance.FE, 0.9130);
        rejectCurve(graphs, Distance.SP_CT, 0.9783);
    }

    public void rejectCurve(GraphBundle graphs, Distance distance, Double param) {
        RejectCurve rq = new RejectCurve();
        Map<String, Map<Double, Double>> result = rq.calcCurve(new DistanceWrapper(distance), param, graphs, 100);
        new TaskPoolResult("rq " + distance.getName(), new ArrayList<>(result.keySet()), result, null).writeData();

    }
}

