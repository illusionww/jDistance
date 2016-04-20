package com.jdistance.gridsearch.statistics;

import com.jdistance.distance.Shortcuts;
import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

public class BasicMeasureStatistics {
    private Double minValue;
    private Double maxValue;
    private Double avgValue;
    private Double avgDiagValue;

    public BasicMeasureStatistics(BasicMeasureStatistics statistics) {
        this(statistics.getMinValue(), statistics.getMaxValue(), statistics.getAvgValue(), statistics.getAvgDiagValue());
    }

    public BasicMeasureStatistics(Double minValue, Double maxValue, Double avgValue) {
        this(minValue, maxValue, avgValue, null);
    }

    private BasicMeasureStatistics(Double minValue, Double maxValue, Double avgValue, Double avgDiagValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.avgValue = avgValue;
        this.avgDiagValue = avgDiagValue;
    }

    public static BasicMeasureStatistics join(List<BasicMeasureStatistics> metricStatisticByGraph) {
        OptionalDouble minValue = metricStatisticByGraph.stream().mapToDouble(BasicMeasureStatistics::getMinValue).average();
        OptionalDouble maxValue = metricStatisticByGraph.stream().mapToDouble(BasicMeasureStatistics::getMaxValue).average();
        OptionalDouble avgValue = metricStatisticByGraph.stream().mapToDouble(BasicMeasureStatistics::getAvgValue).average();
        OptionalDouble avgDiagValue = metricStatisticByGraph.stream().mapToDouble(BasicMeasureStatistics::getAvgDiagValue).average();
        return new BasicMeasureStatistics(minValue.isPresent() ? minValue.getAsDouble() : null,
                maxValue.isPresent() ? maxValue.getAsDouble() : null,
                avgValue.isPresent() ? avgValue.getAsDouble() : null,
                avgDiagValue.isPresent() ? avgDiagValue.getAsDouble() : null);
    }

    public Double getMinValue() {
        return minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public Double getAvgValue() {
        return avgValue;
    }

    public Double getAvgDiagValue() {
        return avgDiagValue;
    }

    public static BasicMeasureStatistics calcMinMaxAvgOfList(List<Double> list) {
        OptionalDouble optionalMin = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p) && p != 0).min();
        Double minValue = optionalMin.isPresent() ? optionalMin.getAsDouble() : null;
        OptionalDouble optionalMax = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p)).max();
        Double maxValue = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
        OptionalDouble optionalAvg = list.stream().mapToDouble(i -> i).filter(p -> !Double.isNaN(p)).average();
        Double avgValue = optionalAvg.isPresent() ? optionalAvg.getAsDouble() : null;
        return new BasicMeasureStatistics(minValue, maxValue, avgValue);
    }

    public static BasicMeasureStatistics calcMinMaxAvgOfMatrix(DenseMatrix D) {
        OptionalDouble optionalMin = Arrays.stream(D.getValues()).filter(p -> !Double.isNaN(p)).min();
        Double minValue = optionalMin.isPresent() ? optionalMin.getAsDouble() : null;
        OptionalDouble optionalMax = Arrays.stream(D.getValues()).filter(p -> !Double.isNaN(p)).max();
        Double maxValue = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
        OptionalDouble optionalAvg = Arrays.stream(D.getValues()).filter(p -> !Double.isNaN(p)).average();
        Double avgValue = optionalAvg.isPresent() ? optionalAvg.getAsDouble() : null;
        OptionalDouble optionalAvgDiag = Arrays.stream(Shortcuts.diagToVector(D).getValues()).filter(p -> !Double.isNaN(p)).average();
        Double avgValueDiag = optionalAvgDiag.isPresent() ? optionalAvgDiag.getAsDouble() : null;
        return new BasicMeasureStatistics(minValue, maxValue, avgValue, avgValueDiag);
    }
}
