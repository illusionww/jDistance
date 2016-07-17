package com.jdistance.spark.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.gridsearch.GridSearch;
import com.jdistance.measure.AbstractMeasureWrapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class SparkGridSearch extends GridSearch {
    private JavaPairRDD<Double, Double> scores;

    public SparkGridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount) {
        super(name, estimator, metricWrapper, scorer, from, to, pointsCount);
    }

    public void fit(GraphBundle graphs) {
        this.graphs = graphs;

        SparkConf conf = new SparkConf().setAppName("SparkGridSearch");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<Double> params = sc.parallelize(paramGrid);
        scores = params
                .mapToPair(idx -> new Tuple2<>(idx, validate(idx, metricWrapper)))
                .cache();
    }

    public JavaPairRDD<Double, Double> getScores() {
        return scores;
    }


}
