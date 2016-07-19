package com.jdistance.spark;

import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.TaskPool;
import com.jdistance.spark.workflow.TaskPoolResult;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TaskPoolTests {
    @Test
    public void testPrepareResults() {
        Map<ImmutablePair<String, Double>, Pair<Double, Double>> sampleData = new HashMap<ImmutablePair<String, Double>, Pair<Double, Double>>() {{
           put(new ImmutablePair<>("firstLine", 0.0), new ImmutablePair<>(0.85, 0.1));
           put(new ImmutablePair<>("firstLine", 0.1), new ImmutablePair<>(0.56, null));
           put(new ImmutablePair<>("firstLine", 0.1), new ImmutablePair<>(0.62, null));
           put(new ImmutablePair<>("secondLine", 0.0), new ImmutablePair<>(null, 0.62));
           put(new ImmutablePair<>("secondLine", 0.1), new ImmutablePair<>(null, null));
           put(new ImmutablePair<>("thirdLine", null), new ImmutablePair<>(null, null));
//           put(new ImmutablePair<>(null, null), new ImmutablePair<>(null, null));
        }};

        JavaPairRDD javaPairRDD = mock(JavaPairRDD.class);
        when(javaPairRDD.collectAsMap()).thenReturn(sampleData);

        JavaRDD javaRDD = mock(JavaRDD.class);
        when(javaRDD.mapToPair(any(PairFunction.class))).thenReturn(javaPairRDD);

        JavaSparkContext sparkContext = mock(JavaSparkContext.class);
        when(sparkContext.parallelize(anyList())).thenReturn(javaRDD);

        Context.fill(sparkContext, "./ivashkin/jDistance");

        TaskPoolResult result = new TaskPool().execute();
        System.out.println(result.getData());
    }
}
