package com.jdistance.local;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Distance;
import com.jdistance.learning.measure.DistanceWrapper;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.GridSearch;
import org.junit.Before;
import org.junit.Test;

public class TaskPoolTests {
    @Before
    public void init() {
        Context.fill(false, "./test", "./test");
    }

    @Test
    public void calcTest() {
        GraphBundle graphs = new GnPInPOutGraphGenerator().generate(new GeneratorPropertiesPOJO(1, 100, 2, 0.3, 0.1));
        new GridSearch()
                .addLine(Distance.FOR.getName(), new Ward(2), new DistanceWrapper(Distance.FOR), Scorer.RI, graphs, 51)
                .execute()
                .writeData()
                .draw();
    }

    @Test
    public void datasetTest() {
        GraphBundle graphs = Dataset.FOOTBALL.get();
        new GridSearch()
                .addLine(Distance.FOR.getName(), new Ward(2), new DistanceWrapper(Distance.FOR), Scorer.RI, graphs, 51)
                .execute()
                .writeData()
                .draw();
    }
}
