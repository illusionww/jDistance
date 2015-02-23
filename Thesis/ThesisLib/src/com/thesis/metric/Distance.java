package com.thesis.metric;

import org.jblas.DoubleMatrix;

public enum Distance {
    WALK("Walk distances", 1.0) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            double t = DistancesBuilder.alphaToT(A, alpha);
            DoubleMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest Distances", Double.MAX_VALUE) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest Distances", Double.MAX_VALUE) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix H = DistancesBuilder.getH0Forest(L, t);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk Distances", 1.0) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            double t = DistancesBuilder.alphaToT(A, alpha);
            DoubleMatrix H = DistancesBuilder.getH0Walk(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances", Double.MAX_VALUE) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix H = DistancesBuilder.getH0Communicability(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability Distances", Double.MAX_VALUE) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DoubleMatrix H0 = DistancesBuilder.getH0Communicability(A, t);
            DoubleMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics", 1.0) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double lambda) {
            if (lambda <= 0 || lambda >= 1) {
                throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
            }

            DoubleMatrix L = DistancesBuilder.getL(A);
            DoubleMatrix Ds = DistancesBuilder.getDShortestPath(A);
            DoubleMatrix H = DistancesBuilder.getHResistance(L);
            DoubleMatrix Dr = DistancesBuilder.getD(H);
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances", Double.MAX_VALUE) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double beta) {
            return DistancesBuilder.getDFreeEnergy(A, beta);
        }
    };

    private String name;
    private Double maxParam;

    Distance(String name, Double maxParam) {
        this.name = name;
        this.maxParam = maxParam;
    }

    public String getName() {
        return name;
    }

    public Double getMaxParam() {
        return maxParam;
    }

    public abstract DoubleMatrix getD(DoubleMatrix A, double t);
}
