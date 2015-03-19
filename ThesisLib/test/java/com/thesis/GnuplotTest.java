package com.thesis;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import jeigen.DenseMatrix;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GnuplotTest {
    DenseMatrix exampleMatrix = new DenseMatrix(new double[][]{
            {0, 1, 0, 0},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {0, 0, 1, 0}
    });

    @Test
    public void testDrawPlot() {
        GNUPlotAdapter ga = new GNUPlotAdapter("C:\\cygwin64\\bin\\gnuplot.exe");

        String imgTitle = "test plots";

        String plot1Title = "test plot 1";
        List<Point<Double>> plot1Points = new ArrayList<>();
        plot1Points.add(new Point<>(1.0, 2.0));
        plot1Points.add(new Point<>(2.0, 1.0));
        plot1Points.add(new Point<>(3.0, 3.0));
        PointDataSet<Double> plot1PointsSet = new PointDataSet<>(plot1Points);
        Plot plot1 = new Plot(plot1Title, NamedPlotColor.RED, plot1PointsSet);

        String plot2Title = "test plot 2";
        List<Point<Double>> plot2Points = new ArrayList<>();
        plot2Points.add(new Point<>(1.5, 1.7));
        plot2Points.add(new Point<>(2.5, 2.3));
        plot2Points.add(new Point<>(3.5, 2.5));
        PointDataSet<Double> pointsSet = new PointDataSet<>(plot2Points);
        Plot plot2 = new Plot(plot2Title, NamedPlotColor.BLUE, pointsSet);

        List<Plot> plots = new ArrayList<>();
        plots.add(plot1);
        plots.add(plot2);

        ga.drawData(imgTitle, plots, "output.png");
    }
}
