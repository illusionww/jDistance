package com.jdistance.local.adapter;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.Point;
import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.PlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Smooth;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GNUPlotAdapter {
    private static final Logger log = LoggerFactory.getLogger(GNUPlotAdapter.class);
    private static final List<RGBAColor> colors = Arrays.asList(
            new RGBAColor("#77FF0000"),
            new RGBAColor("#7700FF00"),
            new RGBAColor("#770000FF"),
            new RGBAColor("#77FFFF00"),
            new RGBAColor("#7700FFFF"),
            new RGBAColor("#77FF00FF"),
            new RGBAColor("#77800000"),
            new RGBAColor("#77008000"),
            new RGBAColor("#77000080"),
            new RGBAColor("#77808000"),
            new RGBAColor("#77008080"),
            new RGBAColor("#77800080"),
            new RGBAColor("#77808080")
    );

    public void draw(String filePath, Map<String, Map<Double, Pair<Double, Double>>> data) {
        Iterator<RGBAColor> color = colors.iterator();
        List<PlotDTO> plots = data.entrySet().stream()
                .map(lineEntry -> new PlotDTO(lineEntry.getKey(), color.next(), new PointDataSet<Double>(lineEntry.getValue().entrySet().stream()
                        .map(pointEntry -> new Point<>(pointEntry.getKey(), pointEntry.getValue().getLeft()))
                        .collect(Collectors.toList()))))
                .collect(Collectors.toList());
        drawData(plots, filePath, "[0:1]", "0.2", "[0:1]", "0.2", Smooth.UNIQUE);
    }

    private void drawData(List<PlotDTO> data, String outputPath, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        ImageTerminal png = new ImageTerminal();
        png.set("size", "3216,2461");
        png.set("enhanced font", "'Verdana,50'");
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
        if (xticks != null) gnuplot.set("xtics", xticks);
        if (yticks != null) gnuplot.set("ytics", yticks);
        gnuplot.set("mxtics", "2");
        gnuplot.set("mytics", "2");
        gnuplot.set("grid mytics ytics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        gnuplot.set("grid mxtics xtics", "lt 1 lc rgb \"#777777\" lw 3, lt 0 lc rgb \"grey\" lw 2");
        if (xrange != null) gnuplot.set("xrange", xrange);
        if (yrange != null) gnuplot.set("yrange", yrange);

        for (PlotDTO plot : data) {
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

        gnuplot.setKey(JavaPlot.Key.BOTTOM_RIGHT);
        gnuplot.plot();
        try {
            ImageIO.write(png.getImage(), "png", file);
        } catch (IOException ex) {
            log.error("Error while writeData image", ex);
        }
    }

    private static class RGBAColor implements PlotColor {
        private String color;

        RGBAColor(String color) {
            this.color = color;
        }

        @Override
        public String getColor() {
            return "rgb \"" + color + "\"";
        }
    }

    private class PlotDTO {
        private String name;
        private PlotColor color;
        private PointDataSet data;

        PlotDTO(String name, PlotColor color, PointDataSet data) {
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
