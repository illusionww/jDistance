package com.jdistance.learning;

import com.jdistance.workflow.Task;

public enum Axis {
    ESTIMATOR() {
        @Override
        public String getFromTask(Task task) {
            return task.getEstimator().getName();
        }
    },
    SCORER() {
        @Override
        public String getFromTask(Task task) {
            return task.getScorer().getName();
        }
    },
    GRAPHS() {
        @Override
        public String getFromTask(Task task) {
            return task.getGraphs().getName();
        }
    },
    MEASURE() {
        @Override
        public String getFromTask(Task task) {
            return task.getMeasure().getName();
        }
    },
    GRAPHSnMEASURE() {
        @Override
        public String getFromTask(Task task) {
            return task.getGraphs().getName() + "__" + task.getMeasure().getName();
        }
    },
    MEASURE_PARAM() {
        @Override
        public String getFromTask(Task task) {
            return task.getMeasureParam().toString();
        }
    };

    public abstract String getFromTask(Task task);
}
