package com.thesis.workflow.checker;

public enum Scale {
    LOG1 { // from 10^-2 to 500
        @Override
        public Double calc(Double i) {
            Double idx = -4.6 + (4.6 + 6.2) * i;
            return Math.exp(idx);
        }
    },
    LOG2 { // from 10^-4 to 20
        @Override
        public Double calc(Double i) {
            Double idx = -9.2 + (9.2 + 3.0) * i;
            return Math.exp(idx);
        }
    },
    LINEAR {
        @Override
        public Double calc(Double i) {
            return i;
        }
    };

    public abstract Double calc(Double i);
}
