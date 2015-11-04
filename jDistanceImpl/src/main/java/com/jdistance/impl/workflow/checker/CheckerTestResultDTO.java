package com.jdistance.impl.workflow.checker;

public class CheckerTestResultDTO {
    private Double total;
    private Double countErrors;
    private Double coloredNodes;

    public CheckerTestResultDTO(Integer total, Integer countErrors, Integer coloredNodes) {
        this.total = (double)total;
        this.countErrors = (double)countErrors;
        this.coloredNodes = (double)coloredNodes;
    }

    public CheckerTestResultDTO(Double total, Double countErrors) {
        this.total = total;
        this.countErrors = countErrors;
        this.coloredNodes = 0.0;
    }

    public CheckerTestResultDTO(Double total, Double countErrors, Double coloredNodes) {
        this.total = total;
        this.countErrors = countErrors;
        this.coloredNodes = coloredNodes;
    }

    public Double getTotal() {
        return total;
    }

    public Double getCountErrors() {
        return countErrors;
    }

    public Double getColoredNodes() {
        return coloredNodes;
    }
}
