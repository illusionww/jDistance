package com.jdistance.local;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.KernelWrapper;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.GridSearch;
import com.jdistance.local.workflow.GridSearchResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Context.fill(true, ".\\results-local\\data", ".\\results-local\\img");
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

    public void trivial() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(3, 100, 2, 0.25, 0.1));
        new GridSearch().addLinesForDifferentMeasures(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Arrays.asList(
                new KernelWrapper(Kernel.COMM_H),
                new KernelWrapper(Kernel.LOG_COMM_H)
        ), graphs, 60)
                .execute()
                .writeData()
                .draw();
    }

    public void datasets() {
        List<Dataset> datasets = Arrays.asList(
                Dataset.news_5cl_1,
                Dataset.news_5cl_2,
                Dataset.news_5cl_3
        );

        for (Dataset dataset : datasets) {
            GraphBundle graphs = dataset.get();
            new GridSearch(dataset.name())
                    .addLinesForDifferentMeasures(
                            new Ward(graphs.getProperties().getClustersCount()),
                            Scorer.ARI,
                            Kernel.getAllH_plusRSP_FE(),
                            graphs,
                            7)
                    .execute()
                    .writeData();
        }
    }

    public void size() {
        Map<String, Map<Double, Pair<Double, Double>>> result = new HashMap<>();
        for (int i = 1; i < 11; i++) {
            Integer graphSize = 10 * i;
            GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(40, graphSize, 2, 0.3, 0.1));
            new GridSearch()
                    .addLinesForDifferentMeasures(
                            new Ward(graphs.getProperties().getClustersCount()),
                            Scorer.ARI,
                            Kernel.getAllH_plusRSP_FE(),
                            graphs,
                            30)
                    .execute()
                    .getData()
                    .forEach((measure, rawMeasureResults) -> {
                        Map<Double, Pair<Double, Double>> measureResults = result.getOrDefault(measure, new HashMap<>());
                        result.put(measure, measureResults);
                        OptionalDouble optionalDouble = rawMeasureResults.entrySet().stream()
                                .filter(measureResult -> measureResult.getValue() != null && measureResult.getValue().getLeft() != null)
                                .mapToDouble(measureResult -> measureResult.getValue().getLeft())
                                .max();
                        if (optionalDouble.isPresent()) {
                            measureResults.put((double) graphSize, new ImmutablePair<>(optionalDouble.getAsDouble(), 0.0));
                        }
                    });
        }
        new GridSearchResult("size", result)
                .writeData()
                .draw();
    }
}

