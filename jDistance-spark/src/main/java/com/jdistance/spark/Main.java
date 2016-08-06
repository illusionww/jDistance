package com.jdistance.spark;

import com.jdistance.Dataset;
import com.jdistance.graph.GraphBundle;
import com.jdistance.learning.Scorer;
import com.jdistance.learning.clustering.Ward;
import com.jdistance.learning.measure.Kernel;
import com.jdistance.learning.measure.KernelWrapper;
import com.jdistance.spark.workflow.Context;
import com.jdistance.spark.workflow.GridSearch;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("jDistance");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        Context.fill(sparkContext, "./ivashkin/jDistance");

        task();

        sparkContext.stop();
    }

    public static void task() {
        List<Dataset> datasets = Arrays.asList(
                Dataset.FOOTBALL,
                Dataset.POLBOOKS,
                Dataset.ZACHARY,
                Dataset.news_2cl_1,
                Dataset.news_2cl_2,
                Dataset.news_2cl_3,
                Dataset.news_3cl_1,
                Dataset.news_3cl_2,
                Dataset.news_3cl_3,
                Dataset.news_5cl_1,
                Dataset.news_5cl_2,
                Dataset.news_5cl_3
        );

        GridSearch gridSearch = new GridSearch("Datasets");
        for (Dataset dataset : datasets) {
            GraphBundle graphs = dataset.get();
            List<KernelWrapper> kernels = Kernel.getAllH_plusRSP_FE();
            for (KernelWrapper kernelWrapper : kernels) {
                kernelWrapper.setName(graphs.getName() + "__" + kernelWrapper.getName());
            }
            gridSearch.addLinesForDifferentMeasures(
                    new Ward(graphs.getProperties().getClustersCount()),
                    Scorer.ARI,
                    kernels,
                    graphs,
                    100);
        }
        gridSearch
                .execute()
                .writeData();
    }
}
