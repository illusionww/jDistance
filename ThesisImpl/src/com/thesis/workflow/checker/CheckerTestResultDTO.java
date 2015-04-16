package com.thesis.workflow.checker;

public class CheckerTestResultDTO {
    private Integer total;
    private Integer countErrors;
    private Integer coloredNodes;

    public CheckerTestResultDTO(Integer total, Integer countErrors, Integer coloredNodes) {
        this.total = total;
        this.countErrors = countErrors;
        this.coloredNodes = coloredNodes;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getCountErrors() {
        return countErrors;
    }

    public Integer getColoredNodes() {
        return coloredNodes;
    }
}
