package com.jdistance.workflow.task.competition;

import com.jdistance.metric.Distance;

import java.util.Map;

public class CompetitionDTO {
    public Distance distance;
    public Integer k;
    public Double x;
    public Map.Entry<Double, Double> pLearn;
    public Integer score;
    public Double tempResult;

    public CompetitionDTO(Distance distance, Integer k, Double x) {
        this.distance = distance;
        this.k = k;
        this.x = x;
    }
}
