package com.jdistance.spark;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.measure.Kernel;
import com.jdistance.measure.KernelWrapper;
import com.jdistance.spark.workflow.SparkTaskPool;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, 100, 2, 0.3, 0.2));
        new SparkTaskPool().buildSimilarTasks(new Ward(graphs.getProperties().getClustersCount()), Scorer.ARI, Arrays.asList(
                new KernelWrapper(Kernel.LOG_COMM_H)
        ), graphs, 35)
                .execute()
                .writeData("\\home\\vivashkin\\example1");
    }
}
