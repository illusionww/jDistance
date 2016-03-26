package com.jdistance.impl.adapter.gnuplot;

import com.jdistance.impl.workflow.context.ContextProvider;
import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.*;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

public class GNUPlotAdapter {
    public static final PlotColor[] colors = {
            NamedPlotColor.BLACK,
            NamedPlotColor.RED,
            NamedPlotColor.BLUE,
            NamedPlotColor.MAGENTA,
            NamedPlotColor.FOREST_GREEN,
            NamedPlotColor.ORANGE,
            NamedPlotColor.VIOLET,
            NamedPlotColor.PURPLE,
            NamedPlotColor.DARK_RED,
            NamedPlotColor.DARK_YELLOW
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

    public void drawData(List<PlotDTO> data, String outputPath, String scriptPath, String yrange, String yticks, Smooth smooth) {
        if (ContextProvider.getContext().getWriteGnuplotScripts()) {
            try {
                GNUPlot.getDebugger().setLevel(40);
                GNUPlot.getDebugger().setWriter(new PrintWriter(scriptPath));
            } catch (FileNotFoundException e) {
                log.error("Can't write script");
            }
        }

        ImageTerminal png = new ImageTerminal();
        png.set("size", "3216,2461");
        png.set("enhanced font", "'Verdana,60'");
        File file = new File(outputPath);
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (IOException ex) {
            log.error("Error while open file for writing", ex);
        }

        JavaPlot gnuplot = gnuplotPath != null ? new JavaPlot(gnuplotPath) : new JavaPlot();
        gnuplot.setTerminal(png);
        gnuplot.set("border", "31 lw 8.0");
        gnuplot.set("xtics", "0,0.2,1");
        gnuplot.set("ytics", yticks);
        gnuplot.set("mxtics", "2");
        gnuplot.set("mytics", "2");
        gnuplot.set("grid mytics ytics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        gnuplot.set("grid mxtics xtics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        gnuplot.set("xrange", "[0.0:1.0]");
        gnuplot.set("yrange", yrange);

        for (PlotDTO plot : data) {
            PlotStyle plotStyle = new PlotStyle();
            plotStyle.setStyle(Style.LINES);
            plotStyle.setLineType(plot.getColor());
            plotStyle.setLineWidth(6);

            DataSetPlot dataSetPlot = new DataSetPlot(plot.getData());
            dataSetPlot.setPlotStyle(plotStyle);
            dataSetPlot.setSmooth(smooth);
            dataSetPlot.setTitle(plot.getName());

            gnuplot.addPlot(dataSetPlot);
        }

        gnuplot.setKey(JavaPlot.Key.TOP_RIGHT);
        gnuplot.plot();
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            log.error("Error while write image", ex);
        }
    }
}
