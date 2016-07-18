package com.jdistance.spark.workflow;

import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.gridsearch.AbstractGridSearch;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class GridSearch extends AbstractGridSearch {
    private JavaPairRDD<Double, Double> scores;

    public GridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount) {
        super(name, estimator, metricWrapper, scorer, from, to, pointsCount);
    }

    @Override
    public void predict(GraphBundle graphs) {
        this.graphs = graphs;

        JavaRDD<Double> params = Context.getInstance().getSparkContext().parallelize(paramGrid);
        scores = params
                .mapToPair(idx -> new Tuple2<>(idx, score(idx, metricWrapper)))
                .cache();
    }

    public JavaPairRDD<Double, Double> getScores() {
        return scores;
    }


}
