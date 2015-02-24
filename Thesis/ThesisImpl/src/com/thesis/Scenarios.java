package com.thesis;

import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.workflow.Checker;
import com.thesis.workflow.ClassifierChecker;
import com.thesis.workflow.ClustererChecker;

import java.util.ArrayList;
import java.util.List;

public class Scenarios {
    public static void drawClassifierCapabilityAllMetricsInRange(List<Graph> graphs, Integer n, Integer p, Double k, Double from, Double to, Double step) {
        String imgTitle = "Classifier: n=" + n + ", p=" + p + ", k=" + k;
        String outputPath = Constants.IMG_FOLDER + "classifier_all_n" + n + "k" + k + "p" + p + ".png";
        ClassifierChecker checker = new ClassifierChecker(graphs, p, k);
        List<Plot> plots = Scenarios.plotAllMetricsInRange(checker, from, to, step);
        GNUPlotAdapter ga = new GNUPlotAdapter(Constants.GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, outputPath);
    }

    public static void drawClustererCapabilityAllMetricsInRange(List<Graph> graphs, Integer n, Integer k, Double from, Double to, Double step) {
        String imgTitle = "Clusterer: n=" + n + ", k=" + k;
        String outputPath = Constants.IMG_FOLDER + "clusterer_all_n" + n + "k" + k + ".png";
        ClustererChecker checker = new ClustererChecker(graphs, k);
        List<Plot> plots = Scenarios.plotAllMetricsInRange(checker, from, to, step);
        GNUPlotAdapter ga = new GNUPlotAdapter(Constants.GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, outputPath);
    }

    public static List<Plot> plotAllMetricsInRange(Checker checker, Double from, Double to, Double step) {
        List<Plot> plots = new ArrayList<>();
        Distance[] distances = Distance.values();
        for (int i = 0; i < distances.length; i++) {
            if (distances[i].equals(Distance.LOGARITHMIC_COMMUNICABILITY))
                continue;
            Plot plot = MetricsHelper.newPlot(checker, distances[i], from, to, step, MetricsHelper.colors[i]);
            plots.add(plot);
        }
        return plots;
    }
}
