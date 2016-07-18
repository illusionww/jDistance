package com.jdistance;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.TaskPool;
import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.DistanceWrapper;
import org.junit.Before;
import org.junit.Test;

public class AllSystemsWorksTests {
    @Before
    public void init() {
        Context.fill(false, "./test", "./test");
    }

    @Test
    public void calcTest() {
        int clustersCount = 4;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, clustersCount, 0.3, 0.1));
        new TaskPool()
                .addLine(Distance.FOR.getName(), new Ward(clustersCount), new DistanceWrapper(Distance.FOR), Scorer.RI, graphs, 51)
                .execute()
                .writeData()
                .draw();
    }
}
