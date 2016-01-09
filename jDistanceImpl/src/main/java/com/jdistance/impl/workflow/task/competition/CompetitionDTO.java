package com.jdistance.impl.workflow.task.competition;

import com.jdistance.metric.MetricWrapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompetitionDTO {
    public MetricWrapper metricWrapper;
    public Integer k;
    public Double x;
    public Map.Entry<Double, Double> pLearn;
    public Integer score;
    public Double tempResult;
    public Map<String, Double> additionalInfo = new LinkedHashMap<>();

    public CompetitionDTO(MetricWrapper metricWrapper, Integer k, Double x) {
        this.metricWrapper = metricWrapper;
        this.k = k;
        this.x = x;
    }

    public CompetitionDTO(MetricWrapper metricWrapper, Integer k) {
        this(metricWrapper, k, 0.0);
    }
}
