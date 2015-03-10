package com.thesis.metric;

import org.jblas.DoubleMatrix;

public enum Distance {
    WALK("Walk", "Walk", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            DistancesBuilder db = new DistancesBuilder();
            double t = db.alphaToT(A, alpha);
            DoubleMatrix H0 = db.getH0Walk(A, t);
            DoubleMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest", "logFor", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DoubleMatrix L = db.getL(A);
            DoubleMatrix H0 = db.getH0Forest(L, t);
            DoubleMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest", "For", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DoubleMatrix L = db.getL(A);
            DoubleMatrix H = db.getH0Forest(L, t);
            return db.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk", "pWalk", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double alpha) {
            DistancesBuilder db = new DistancesBuilder();
            double t = db.alphaToT(A, alpha);
            DoubleMatrix H = db.getH0Walk(A, t);
            return db.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances", "Comm", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DoubleMatrix H = db.getH0Communicability(A, t);
            DoubleMatrix D = db.getD(H);
            return db.sqrtD(D);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability", "logComm", Scale.LOG1) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DoubleMatrix H0 = db.getH0Communicability(A, t);
            DoubleMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics", "SP-CT", Scale.LINEAR) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double lambda) {
            if (lambda < 0.0 || lambda > 1.0) {
                throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
            }

            DistancesBuilder db = new DistancesBuilder();
            DoubleMatrix L = db.getL(A);
            DoubleMatrix Ds = db.getDShortestPath(A);
            DoubleMatrix H = db.getHResistance(L);
            DoubleMatrix Dr = db.getD(H);
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances", "FE", Scale.LOG2) {
        @Override
        public DoubleMatrix getD(DoubleMatrix A, double beta) {
            DistancesBuilder db = new DistancesBuilder();
            return db.getDFreeEnergy(A, beta);
        }
    };

    private volatile String name;
    private volatile String shortName;
    private volatile Scale scale;

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
