package com.jdistance;

import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.LocalTaskPool;
import com.jdistance.measure.Distance;
import com.jdistance.measure.DistanceWrapper;
import org.junit.Before;
import org.junit.Test;

public class AllSystemsWorksTests {
    @Before
    public void init() {
        Context.fill(false, false, "./test", "./test");
    }

    @Test
    public void calcTest() {
        int clustersCount = 4;
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, clustersCount, 0.3, 0.1));
        new LocalTaskPool("test")
                .addTask(new Ward(clustersCount), Scorer.RI, new DistanceWrapper(Distance.FOR), graphs, 51)
                .execute()
                .writeData()
                .drawUniqueAndBezier("[0:1]", "0.2");
    }
}
