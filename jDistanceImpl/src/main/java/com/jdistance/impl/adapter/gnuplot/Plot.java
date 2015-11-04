package com.jdistance.impl.adapter.gnuplot;

import com.panayotis.gnuplot.dataset.PointDataSet;
import com.panayotis.gnuplot.style.PlotColor;

public class Plot {
    private String name;
    private PlotColor color;
    private PointDataSet data;

    public Plot(String name, PlotColor color, PointDataSet data) {
        this.name = name;
        this.color = color;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public PlotColor getColor() {
        return color;
    }

    public PointDataSet getData() {
        return data;
    }
}
