package com.thesis;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotColor;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.metric.Distance;
import com.thesis.utils.ArrayUtils;
import com.thesis.workflow.Checker;

import java.util.List;
import java.util.Map;

public class MetricsHelper {
    public static final PlotColor[] colors = {
            NamedPlotColor.RED,
            NamedPlotColor.GREEN,
            NamedPlotColor.BLUE,
            NamedPlotColor.YELLOW,
            NamedPlotColor.GREY,
            NamedPlotColor.BLACK,
            NamedPlotColor.ORANGE,
            NamedPlotColor.VIOLET
    };

    public static Plot newPlot(Checker checker, Distance distance, Double from, Double to, Double step, PlotColor color) {
        String plotTitle = distance.getName();
        Map<Double, Double> results = checker.seriesOfTests(distance, from, to, step);
        List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(results);
        PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
        return new Plot(plotTitle, color, plotPointsSet);
    }
}
