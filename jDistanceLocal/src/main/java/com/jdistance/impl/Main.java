package com.jdistance.impl;

import com.jdistance.distance.AbstractMeasureWrapper;
import com.jdistance.distance.Kernel;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.impl.competitions.CopelandsMethod;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.KMeans;
import com.jdistance.learning.clustering.Ward;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SAXException, IOException, TransformerConfigurationException {
        Context.fill(false, true, true, "./results/data", "./results/img");
        competitonsWard();
        competitonsKMeans();
        competitonsWardAndKMeans();
    }

    private static List<GeneratorPropertiesPOJO> generateGenerationProperties() {
        List<GeneratorPropertiesPOJO> properties = new ArrayList<>();
        for (int nodesCount : new int[]{100}) {
            for (int clustersCount : new int[]{2, 4}) {
                for (double pOut : new double[]{0.15, 0.1}) {
                    properties.add(new GeneratorPropertiesPOJO(15, nodesCount, clustersCount, 0.3, pOut));
                }
            }
        }
        for (int nodesCount : new int[]{150}) {
            for (int clustersCount : new int[]{2, 4}) {
                for (double pOut : new double[]{0.15, 0.1}) {
                    properties.add(new GeneratorPropertiesPOJO(5, nodesCount, clustersCount, 0.3, pOut));
                }
            }
        }
        return properties;
    }

    private static void competitonsWard() {
        List<GeneratorPropertiesPOJO> properties = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(properties, Kernel.getDefaultKernels(), 65, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", Ward", new Ward(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("Ward");
    }

    private static void competitonsKMeans() {
        List<GeneratorPropertiesPOJO> properties = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(properties, Kernel.getDefaultKernels(), 65, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", K-Means", new KMeans(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("K-Means");
    }

    private static void competitonsWardAndKMeans() {
        List<GeneratorPropertiesPOJO> properties = generateGenerationProperties();
        CopelandsMethod copelandsMethod = new CopelandsMethod(properties, Kernel.getDefaultKernels(), 65, 90) {
            @Override
            protected TaskPool generateTaskPool(GraphBundle graphs) {
                TaskPool pool = new TaskPool();
                for (AbstractMeasureWrapper kernel : measures) {
                    pool.addTask(new Task(kernel.getName() + ", Ward", new Ward(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                    pool.addTask(new Task(kernel.getName() + ", K-Means", new KMeans(graphs.getProperties().getClustersCount()), Scorer.RATE_INDEX, kernel, graphs, pointsCount));
                }
                return pool;
            }
        };
        copelandsMethod.execute();
        copelandsMethod.write("Ward and K-Means");
    }
}

