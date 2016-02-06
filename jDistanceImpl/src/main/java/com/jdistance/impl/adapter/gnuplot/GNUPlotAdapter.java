package com.jdistance.impl.adapter.gnuplot;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class GNUPlotAdapter {
    public static final PlotColor[] colors = {
            NamedPlotColor.RED,
            NamedPlotColor.GREEN,
            NamedPlotColor.BLUE,
            NamedPlotColor.YELLOW,
            NamedPlotColor.GREY,
            NamedPlotColor.BLACK,
            NamedPlotColor.ORANGE,
            NamedPlotColor.VIOLET,
            NamedPlotColor.CYAN,
            NamedPlotColor.BROWN,

            NamedPlotColor.BLUE,
            NamedPlotColor.YELLOW,
            NamedPlotColor.GREY,
            NamedPlotColor.BLACK,
            NamedPlotColor.ORANGE,
            NamedPlotColor.VIOLET
    };
    private static final Logger log = LoggerFactory.getLogger(GNUPlotAdapter.class);
    private String gnuplotPath;

    public GNUPlotAdapter(String gnuplotPath) {
        this.gnuplotPath = gnuplotPath;
    }

    public static List<Point<Double>> mapToPoints(Map<Double, Double> results) {
        List<Point<Double>> list = new ArrayList<>();
        SortedSet<Double> keys = new TreeSet<>(results.keySet());
        for (Double key : keys) {
            Double value = results.get(key);
            list.add(new Point<>(key, value));
        }
        return list;
    }

    public void drawData(String title, List<PlotDTO> data, String outputPath) {
        ImageTerminal png = new ImageTerminal();
        png.set("size", "1280,768");
        File file = new File(outputPath);
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (IOException ex) {
            log.error("Error while open file for writing", ex);
        }

        JavaPlot gnuplot = new JavaPlot(gnuplotPath);
        gnuplot.setTerminal(png);

        for (PlotDTO plot : data) {
            PlotStyle plotStyle = new PlotStyle();
            plotStyle.setStyle(Style.LINES);
            plotStyle.setLineType(plot.getColor());
            plotStyle.setLineWidth(2);

            DataSetPlot dataSetPlot = new DataSetPlot(plot.getData());
            dataSetPlot.setPlotStyle(plotStyle);
            dataSetPlot.setTitle(plot.getName());

            gnuplot.addPlot(dataSetPlot);
        }

        gnuplot.setTitle(title);
        gnuplot.plot();

        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            log.error("Error while write image", ex);
        }
    }
}
