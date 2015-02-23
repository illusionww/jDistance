package com.thesis;

import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotColor;
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
    static ClassifierChecker checker;

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        Parser parser = new GraphMLParser(Constants.GRAPHML_EXAMPLE2);
        Graph graph = parser.parse();

        String imgTitle = "Plot";
        checker = new ClassifierChecker(graph, 10, 0.4);
        List<Plot> plots = new ArrayList<>();
        plots.add(newPlot(Distance.WALK, 0.001, 1.0, 0.05, NamedPlotColor.RED));
        plots.add(newPlot(Distance.LOGARITHMIC_FOREST, 0.001, 3.0, 0.05, NamedPlotColor.GREEN));
        plots.add(newPlot(Distance.PLAIN_FOREST, 0.001, 3.0, 0.05, NamedPlotColor.BLUE));
        plots.add(newPlot(Distance.PLAIN_WALK, 0.001, 1.0, 0.05, NamedPlotColor.YELLOW));
        plots.add(newPlot(Distance.COMMUNICABILITY, 0.001, 3.0, 0.05, NamedPlotColor.GREY));
        plots.add(newPlot(Distance.LOGARITHMIC_COMMUNICABILITY, 0.001, 3.0, 0.05, NamedPlotColor.BLACK));
        plots.add(newPlot(Distance.COMBINATIONS, 0.001, 1.0, 0.05, NamedPlotColor.ORANGE));
        plots.add(newPlot(Distance.HELMHOLTZ_FREE_ENERGY, 0.001, 3.0, 0.05, NamedPlotColor.VIOLET));

        GNUPlotAdapter ga = new GNUPlotAdapter(Constants.GNUPLOT_PATH);
        ga.drawData(imgTitle, plots, Constants.PNG_OUTPUT_PATH);
    }

    public static Plot newPlot(Distance distance, Double from, Double to, Double step, PlotColor color) {
        String plotTitle = distance.getName();
        Map<Double, Double> results = checker.seriesOfTests(distance, from, to, step);
        List<Point<Double>> plotPoints = ArrayUtils.mapToPoints(results);
        PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
        return new Plot(plotTitle, color, plotPointsSet);
    }
}

