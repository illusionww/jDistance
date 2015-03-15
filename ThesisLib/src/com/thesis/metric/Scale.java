package com.thesis.metric;

public enum Scale {
    LOG1 { // log from 10^-2 to 500
        @Override
        public Double calc(Double i) {
            Double idx = -4.6 + (4.6 + 6.2) * i;
            return Math.exp(idx);
        }
    },
    LOG2 { // log from 10^-4 to 20
        @Override
        public Double calc(Double i) {
            Double idx = -9.2 + (9.2 + 3.0) * i;
            return Math.exp(idx);
        }
    },
    ATAN { // 2/PI*atan(i) : [0, inf] -> [0, 1]
        @Override
        public Double calc(Double i) {
            return Math.tan(Math.PI/2.0*i);
        }
    },
    EXP { // 1 - exp{-i} : [0, inf] -> [0, 1]
        @Override
        public Double calc(Double i) {
            return -Math.log(1 - i);
        }
    },
    LINEAR {
        @Override
        public Double calc(Double i) {
            return i;
        }
    },
    DEFAULT {
        @Override
        public Double calc(Double i) {
            throw new UnsupportedOperationException("This method can not be called");
        }
    };

    public abstract Double calc(Double i);
}
