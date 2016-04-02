package com.jdistance.metric;

public class MetricWrapper extends AbstractDistanceWrapper {
    private Metric metric;

    public MetricWrapper(Metric metric) {
        super(metric.getName(), metric.getScale(), false);
        this.metric = metric;
    }

    public MetricWrapper(String name, Metric metric) {
        super(name, metric.getScale(), false);
        this.metric = metric;
    }

    public MetricWrapper(String name, Scale scale, Metric metric) {
        super(name, scale, false);
        this.metric = metric;
    }

    public Metric getMetric() {
        return metric;
    }
}
