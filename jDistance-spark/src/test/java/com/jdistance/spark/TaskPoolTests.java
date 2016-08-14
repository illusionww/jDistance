package com.jdistance.spark;

import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.GridSearch;
import com.jdistance.spark.workflow.GridSearchResult;
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
           put(new ImmutablePair<>("firstLine", 0.1), null);
        }};

        JavaPairRDD javaPairRDD = mock(JavaPairRDD.class);
        when(javaPairRDD.collectAsMap()).thenReturn(sampleData);

        JavaRDD javaRDD = mock(JavaRDD.class);
        when(javaRDD.mapToPair(any(PairFunction.class))).thenReturn(javaPairRDD);

        JavaSparkContext sparkContext = mock(JavaSparkContext.class);
        when(sparkContext.parallelize(anyList())).thenReturn(javaRDD);

        Context.fill(sparkContext, "./ivashkin/jDistance");

        GridSearchResult result = new GridSearch().execute();
        System.out.println(result.getData());
    }
}
