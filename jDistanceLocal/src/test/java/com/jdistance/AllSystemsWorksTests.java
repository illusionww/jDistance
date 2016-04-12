package com.jdistance;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.Task;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.Ward;
import com.jdistance.metric.Metric;
import com.jdistance.metric.MetricWrapper;
import org.junit.Before;
import org.junit.Test;

public class AllSystemsWorksTests {
    @Before
    public void init() {
        Context.fill(false, false, true, "./test", "./test");
    }

    @Test
    public void calcTest() {
        int clustersCount = 4;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, clustersCount, 0.3, 0.1));
        Task task = new Task(new Ward(clustersCount), Scorer.RATE_INDEX, new MetricWrapper(Metric.FOREST), graphs, 51);
        new TaskPool("test", task).execute().writeData().writeStatistics().drawUniqueAndBezier("[0:1]", "0.2");
    }
}
