package com.jdistance.spark.workflow;

import com.jdistance.distance.AbstractMeasureWrapper;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Estimator;
import com.jdistance.learning.Scorer;
import jeigen.DenseMatrix;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class GridSearch implements Serializable {
    private String name;
    private Estimator estimator;
    private List<Double> paramGrid;
    private AbstractMeasureWrapper metricWrapper;
    private GraphBundle graphs;
    private Scorer scorer;

    public GridSearch(String name, Estimator estimator, AbstractMeasureWrapper metricWrapper, Scorer scorer, double from, double to, int pointsCount) {
        this.name = name;
        this.estimator = estimator;
        double step = (to - from) / (pointsCount - 1);
        this.paramGrid = DoubleStream.iterate(from, i -> i + step).limit(pointsCount).boxed().collect(Collectors.toList());
        paramGrid.addAll(Arrays.asList(0.1 * step, 0.5 * step, to - 0.5 * step, to - 0.1 * step));
        Collections.sort(paramGrid);
        this.metricWrapper = metricWrapper;
        this.scorer = scorer;
    }

    public void fit(GraphBundle graphs, String outputPath) {
        this.graphs = graphs;

        SparkConf conf = new SparkConf().setAppName("GridSearch");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<Double> params = sc.parallelize(paramGrid);
        JavaPairRDD<Double, Double> results = params.mapToPair(idx -> new Tuple2<>(idx, validate(metricWrapper, idx)));
        results.saveAsTextFile(outputPath);
    }

    private Double validate(AbstractMeasureWrapper metricWrapper, Double idx) {
        List<Double> scoresByGraph = new ArrayList<>();
        try {
            for (Graph graph : graphs.getGraphs()) {
                DenseMatrix A = graph.getA();
                Double parameter = metricWrapper.getScale().calc(A, idx);
                DenseMatrix D = metricWrapper.calc(A, parameter);
                if (!hasNaN(D)) {
                    Map<Integer, Integer> prediction = estimator.predict(D);
                    double score = scorer.score(D, graph.getNodes(), prediction);
                    scoresByGraph.add(score);
                }
            }
        } catch (RuntimeException e) {
            System.err.println("Calculation error: distance " + metricWrapper.getName() + ", gridParam " + idx);
        }

        if (scoresByGraph.size() < 0.9 * graphs.getGraphs().size()) {
            return null;
        }
        double avg = avg(scoresByGraph);
        return avg != 0.0 ? avg : null;
    }

    private double avg(List<Double> scoresByGraph) {
        double sum = 0.0;
        for (double score : scoresByGraph) {
            sum += score;
        }
        return sum / scoresByGraph.size();
    }

    private boolean hasNaN(DenseMatrix D) {
        for (double item : D.getValues()) {
            if (Double.isNaN(item)) {
                return true;
            }
        }
        return false;
    }
}
