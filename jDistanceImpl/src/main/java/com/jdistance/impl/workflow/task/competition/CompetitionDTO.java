package com.jdistance.impl.workflow.task.competition;

import com.jdistance.metric.Distance;

import java.util.LinkedHashMap;
import java.util.Map;

public class CompetitionDTO {
    public Distance distance;
    public Integer k;
    public Double x;
    public Map.Entry<Double, Double> pLearn;
    public Integer score;
    public Double tempResult;
    public Map<String, Double> additionalInfo = new LinkedHashMap<>();


    public CompetitionDTO(Distance distance, Integer k) {
        this.distance = distance;
        this.k = k;
        this.x = 0d;
    }

    public CompetitionDTO(Distance distance, Integer k, Double x) {
        this.distance = distance;
        this.k = k;
        this.x = x;
    }
}
