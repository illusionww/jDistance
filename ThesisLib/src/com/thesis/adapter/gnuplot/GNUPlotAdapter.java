package com.thesis.adapter.gnuplot;

import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class GNUPlotAdapter {
    private String gnuplotPath;

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

    public GNUPlotAdapter(String gnuplotPath) {
        this.gnuplotPath = gnuplotPath;
    }

    public void drawData(String title, List<Plot> data, String outputPath) {
        ImageTerminal png = new ImageTerminal();
        png.set("size", "1280,960");
        File file = new File(outputPath);
        try {
            file.createNewFile();
            png.processOutput(new FileInputStream(file));
        } catch (IOException ex) {
            System.err.print(ex);
        }

        JavaPlot gnuplot = new JavaPlot(gnuplotPath);
        gnuplot.setTerminal(png);

        for(Plot plot : data) {
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
            System.err.print(ex);
        }
    }
}
