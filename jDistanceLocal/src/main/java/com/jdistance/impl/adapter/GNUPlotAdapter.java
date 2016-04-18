package com.jdistance.impl.adapter;

import com.jdistance.impl.workflow.Context;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.*;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class GNUPlotAdapter {
    private static final Logger log = LoggerFactory.getLogger(GNUPlotAdapter.class);
    private static final List<PlotColor> colors = Arrays.asList(
            NamedPlotColor.RED,
            NamedPlotColor.BLUE,
            NamedPlotColor.GREEN,
            NamedPlotColor.CYAN,
            NamedPlotColor.DARK_VIOLET,
            NamedPlotColor.ORANGE,
            NamedPlotColor.YELLOW,
            NamedPlotColor.LIGHT_GREEN,
            NamedPlotColor.GREY50,
            NamedPlotColor.MAGENTA,
            NamedPlotColor.BROWN
    );

    public void draw(List<String> taskNames, Map<String, Map<Double, Double>> data, String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        Iterator<PlotColor> color = colors.iterator();

        List<PlotPOJO> plots = new ArrayList<>();
        taskNames.forEach(taskName -> {
            List<Point<Double>> plotPoints = mapToPoints(data.get(taskName));
            PointDataSet<Double> plotPointsSet = new PointDataSet<>(plotPoints);
            plots.add(new PlotPOJO(taskName, color.next(), plotPointsSet));
        });

        drawData(plots, Context.getInstance().buildImgFullName(smooth.toString(), imgTitle, "png"), xrange, xticks, yrange, yticks, smooth);
    }

    private List<Point<Double>> mapToPoints(Map<Double, Double> results) {
        List<Point<Double>> list = new ArrayList<>();
        if (results != null) {
            SortedSet<Double> keys = new TreeSet<>(results.keySet());
            for (Double key : keys) {
                Double value = results.get(key);
                list.add(new Point<>(key, value));
            }
        }
        return list;
    }

    private void drawData(List<PlotPOJO> data, String outputPath, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
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

        JavaPlot gnuplot = new JavaPlot();
        gnuplot.setTerminal(png);
        gnuplot.set("border", "31 lw 8.0");
        gnuplot.set("xtics", xticks);
        gnuplot.set("ytics", yticks);
        gnuplot.set("mxtics", "2");
        gnuplot.set("mytics", "2");
        gnuplot.set("grid mytics ytics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        gnuplot.set("grid mxtics xtics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        gnuplot.set("xrange", xrange);
        gnuplot.set("yrange", yrange);

        for (PlotPOJO plot : data) {
            PlotStyle plotStyle = new PlotStyle();
            plotStyle.setStyle(Style.LINES);
            plotStyle.setLineType(plot.getColor());
            plotStyle.setLineWidth(7);

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
            log.error("Error while writeData image", ex);
        }
    }

    private class PlotPOJO {
        private String name;
        private PlotColor color;
        private PointDataSet data;

        PlotPOJO(String name, PlotColor color, PointDataSet data) {
            this.name = name;
            this.color = color;
            this.data = data;
        }

        String getName() {
            return name;
        }

        PlotColor getColor() {
            return color;
        }

        PointDataSet getData() {
            return data;
        }
    }
}
