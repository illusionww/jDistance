package com.jdistance.measure;

import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;

import static com.jdistance.measure.Shortcuts.abs;
import static org.jblas.Eigen.eigenvalues;

public enum Scale {
    LINEAR { // SP-CT
        @Override
        public Double calc(DoubleMatrix A, Double t) {
            return t;
        }
    },
    alphaToT { // α > 0 -> 0 < t < α^{-1}
        @Override
        public Double calc(DoubleMatrix A, Double alpha) {
            ComplexDoubleMatrix cfm = eigenvalues(A);
            double rho = abs(cfm).max();
            return 1.0 / (1.0 / alpha + rho);
        }
    },
    RHO { // pWalk, Walk
        @Override
        public Double calc(DoubleMatrix A, Double t) {
            ComplexDoubleMatrix cfm = eigenvalues(A);
            double rho = abs(cfm).max();
            return t / rho;
        }
    },
    FRACTION { // Forest, logForest, Comm, logComm, Heat, logHeat
        @Override
        public Double calc(DoubleMatrix A, Double t) {
            return t / (1.0 - t);
        }
    },
    FRACTION_REVERSED { // RSP, FE
        @Override
        public Double calc(DoubleMatrix A, Double beta) {
            return (1.0 - beta) / beta;
        }
    };

    public abstract Double calc(DoubleMatrix A, Double t);
}
