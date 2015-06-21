package com.thesis.metric;

import jeigen.ComplexDenseMatrix;
import jeigen.DenseMatrix;

public enum Scale {
    alphaToT { // ? > 0 -> 0 < t < ?^{-1}
        @Override
        public Double calc(DenseMatrix A, Double alpha) {
            ComplexDenseMatrix cfm = new ComplexDenseMatrix(A.eig().values);
            double ro = cfm.abs().maxOverCols().s();
            return 1.0 / (1.0 / alpha + ro);
        }
    },
    RHO { //walk, pWalk
        @Override
        public Double calc(DenseMatrix A, Double t) {
            ComplexDenseMatrix cfm = new ComplexDenseMatrix(A.eig().values);
            double rho = cfm.abs().maxOverCols().s();
            return t / rho;
        }
    },
    LINEAR { // SP-CT
        @Override
        public Double calc(DenseMatrix A, Double t) {
            return t;
        }
    },
    FRACTION { // forest, logForest, comm, logComm
        @Override
        public Double calc(DenseMatrix A, Double t) {
            return t / (1.0 - t);
        }
    },
    FRACTION_BETA { // FE
        @Override
        public Double calc(DenseMatrix A, Double t) {
            return (1.0 - t) / t;
        }
    };

    public abstract Double calc(DenseMatrix A, Double t);
}
