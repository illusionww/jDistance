package com.thesis.metric;


import jeigen.DenseMatrix;

public enum Distance {
    WALK("Walk", "Walk", Scale.LINEAR) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double alpha) {
            DistancesBuilder db = new DistancesBuilder();
            double t = db.alphaToT(A, alpha);
            DenseMatrix H0 = db.getH0Walk(A, t);
            DenseMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest", "logFor", Scale.DEFAULT) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix H0 = db.getH0Forest(L, t);
            DenseMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest", "For", Scale.DEFAULT) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix H = db.getH0Forest(L, t);
            return db.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk", "pWalk", Scale.LINEAR) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double alpha) {
            DistancesBuilder db = new DistancesBuilder();
            double t = db.alphaToT(A, alpha);
            DenseMatrix H = db.getH0Walk(A, t);
            return db.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances", "Comm", Scale.DEFAULT) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DenseMatrix H = db.getH0Communicability(A, t);
            DenseMatrix D = db.getD(H);
            return db.sqrtD(D);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability", "logComm", Scale.DEFAULT) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DistancesBuilder db = new DistancesBuilder();
            DenseMatrix H0 = db.getH0Communicability(A, t);
            DenseMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics", "SP-CT", Scale.LINEAR) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double lambda) {
            if (lambda < 0.0 || lambda > 1.0) {
                throw new RuntimeException("lambda should be in [0, 1], lambda = " + lambda);
            }

            DistancesBuilder db = new DistancesBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix Ds = db.getDShortestPath(A);
            DenseMatrix H = db.getHResistance(L);
            DenseMatrix Dr = db.getD(H);
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances", "FE", Scale.DEFAULT) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double beta) {
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

    public abstract DenseMatrix getD(DenseMatrix A, double t);

    public String toString() {
        return shortName;
    }
}
