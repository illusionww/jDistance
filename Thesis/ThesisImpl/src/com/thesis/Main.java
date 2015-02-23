package com.thesis;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.thesis.adapter.gnuplot.GNUPlotAdapter;
import com.thesis.adapter.gnuplot.Plot;
import com.thesis.adapter.parser.GraphMLParser;
import com.thesis.adapter.parser.Parser;
import com.thesis.graph.Graph;
import com.thesis.metric.Distance;
import com.thesis.utils.ArrayUtils;
import com.thesis.workflow.ClassifierChecker;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Parser parser = new GraphMLParser(Constants.GRAPHML_EXAMPLE2);
        Graph graph = parser.parse();

        String imgTitle = "Plot";
        ClassifierChecker o = new ClassifierChecker(graph, 10, 0.4);
        List<Plot> plots = new ArrayList<>();

        Distance distance1 = Distance.WALK;
        Map<Double, Double> results1 = o.seriesOfTests(distance1, 0.001, 1.0, 0.05);
        String plot1Title = distance1.getName();
        List<Point<Double>> plot1Points = ArrayUtils.mapToPoints(results1);
        PointDataSet<Double> plot1PointsSet = new PointDataSet<>(plot1Points);
        Plot plot1 = new Plot(plot1Title, NamedPlotColor.RED, plot1PointsSet);
        plots.add(plot1);

        Distance distance2 = Distance.HELMHOLTZ_FREE_ENERGY;
        Map<Double, Double> results2 = o.seriesOfTests(distance2, 0.001, 2.0, 0.1);
        String plot2Title = distance2.getName();
        List<Point<Double>> plot2Points = ArrayUtils.mapToPoints(results2);
        PointDataSet<Double> plot2PointsSet = new PointDataSet<>(plot2Points);
        Plot plot2 = new Plot(plot2Title, NamedPlotColor.BLUE, plot2PointsSet);
        plots.add(plot2);

        GNUPlotAdapter ga = new GNUPlotAdapter(Constants.GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Constants.PNG_OUTPUT_PATH);
    }
}

