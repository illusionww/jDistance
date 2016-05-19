package com.jdistance.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("Simple Application");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> textFile = sc.parallelize(Arrays.asList(
                "asdas sadasdc svcsdsdc",
                "ascasc ascas ascas asca dvdvdvdvdv dvascc ascas ascas"
        ));
        JavaRDD<String> words = textFile.flatMap(s -> Arrays.asList(s.split(" ")));
        JavaPairRDD<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));
        JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
        counts.saveAsTextFile("/user/pdc/ivashkin/spark-java/articles-part");
    }
}
