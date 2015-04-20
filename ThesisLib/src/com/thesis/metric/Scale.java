package com.thesis.metric;

public enum Scale {
    LINEAR { // walk, pWalk, SP-CT
        @Override
        public Double calc(Double t) {
            return t;
        }
    },
    FRACTION { // forest, logForest, comm, logComm
        @Override
        public Double calc(Double t) {
            return t / (1.0 - t);
        }
    },
    FRACTION_BETA { // FE
        @Override
        public Double calc(Double t) {
            return (1.0 / t - 1.0);
        }
    };

    public abstract Double calc(Double t);
}
