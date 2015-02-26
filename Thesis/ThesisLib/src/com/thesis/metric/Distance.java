package com.thesis.metric;

import com.thesis.workflow.checker.Scale;
import org.jblas.DoubleMatrix;

public enum Distance {
    WALK("Walk", "Walk", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            double t = DistancesBuilder.alphaToT(A, alpha);
            DoubleMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest", "logFor", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest", "For", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix H = DistancesBuilder.getH0Forest(L, t);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk", "pWalk", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            double t = DistancesBuilder.alphaToT(A, alpha);
            DoubleMatrix H = DistancesBuilder.getH0Walk(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances", "Comm", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix H = DistancesBuilder.getH0Communicability(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability", "logComm", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix H0 = DistancesBuilder.getH0Communicability(A, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics", "SP-CT", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double lambda) {
            if (lambda < 0.0 || lambda > 1.0) {
                throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
            }

            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix Ds = DistancesBuilder.getDShortestPath(A);
            DoubleMatrix H = DistancesBuilder.getHResistance(L);
            DoubleMatrix Dr = DistancesBuilder.getD(H);
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances", "FE", Scale.LOG2) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double beta) {
            return DistancesBuilder.getDFreeEnergy(A, beta);
        }
    };

    private String name;
    private String shortName;
    private Scale scale;

    Distance(String name, String shortName, Scale scale) {
        this.name = name;
        this.shortName = shortName;
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Scale getScale() {
        return scale;
    }

    public abstract DoubleMatrix getD(DoubleMatrix A, double t);
}
