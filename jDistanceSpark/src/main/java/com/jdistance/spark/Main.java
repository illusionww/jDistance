package com.jdistance.spark;

import com.jdistance.distance.Kernel;
import com.jdistance.distance.KernelWrapper;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.spark.workflow.Task;

public class Main {
    public static void main(String[] args) {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(10, 100, 2, 0.3, 0.2));
        Task task = new Task(new Ward(2), Scorer.RATE_INDEX, new KernelWrapper(Kernel.LOG_COMM_K), graphs, 36);
        task.execute(args[0]);
    }
}
