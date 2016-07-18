package com.jdistance.measure;

import jeigen.DenseMatrixComplex;
import jeigen.DenseMatrix;

public enum Scale {
    LINEAR { // SP-CT
        @Override
        public Double calc(DenseMatrix A, Double t) {
            return t;
        }
    },
    alphaToT { // α > 0 -> 0 < t < α^{-1}
        @Override
        public Double calc(DenseMatrix A, Double alpha) {
            DenseMatrixComplex cfm = A.eig().values;
            double rho = cfm.abs().maxOverCols().s();
            return 1.0 / (1.0 / alpha + rho);
        }
    },
    RHO { // pWalk, Walk
        @Override
        public Double calc(DenseMatrix A, Double t) {
            DenseMatrixComplex cfm = A.eig().values;
            double rho = cfm.abs().maxOverCols().s();
            return t / rho;
        }
    },
    FRACTION { // Forest, logForest, Comm, logComm, Heat, logHeat
        @Override
        public Double calc(DenseMatrix A, Double t) {
            return t / (1.0 - t);
        }
    },
    FRACTION_REVERSED { // RSP, FE
        @Override
        public Double calc(DenseMatrix A, Double beta) {
            return (1.0 - beta) / beta;
        }
    };

    public abstract Double calc(DenseMatrix A, Double t);
}
