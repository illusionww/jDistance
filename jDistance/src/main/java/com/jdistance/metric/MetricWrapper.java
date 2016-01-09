package com.jdistance.metric;

public class MetricWrapper {
    private String name;
    private Scale scale;
    private Metric metric;

    public MetricWrapper(Metric metric) {
        this.name = metric.getName();
        this.scale = metric.getScale();
        this.metric = metric;
    }

    public MetricWrapper(String name, Scale scale, Metric metric) {
        this.name = name;
        this.scale = scale;
        this.metric = metric;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public Metric getMetric() {
        return metric;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }
}
