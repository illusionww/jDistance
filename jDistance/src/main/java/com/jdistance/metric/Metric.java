package com.jdistance.metric;

import com.jdistance.utils.MatrixUtils;
import com.jdistance.utils.NodesDistanceDTO;
import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;

public enum Metric {
    PLAIN_WALK("Plain Walk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix H = db.getH0Walk(A, t);
            return db.getD(H);
        }
    },
    WALK("Walk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix H0 = db.getH0Walk(A, t);
            DenseMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    FOREST("For", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix H = db.getH0Forest(L, t);
            return db.getD(H);
        }
    },
    LOG_FOREST("logFor", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix H0 = db.getH0Forest(L, t);
            DenseMatrix H = db.H0toH(H0);
            return db.getD(H);
        }
    },
    COMM("Comm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix H = db.getH0Communicability(A, t);
            DenseMatrix D = db.getD(H);
            return db.sqrtD(D);
        }
    },
    LOG_COMM("logComm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix H0 = db.getH0Communicability(A, t);
            DenseMatrix H = db.H0toH(H0);
            DenseMatrix D = db.getD(H);
            return db.sqrtD(D);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR) {
        public DenseMatrix getD(DenseMatrix A, double lambda) {
            JeigenBuilder db = new JeigenBuilder();
            DenseMatrix L = db.getL(A);
            DenseMatrix Ds = db.getDShortestPath(A);
            DenseMatrix H = db.getHResistance(L);
            DenseMatrix Dr = db.getD(H);
            Double avgDs = Ds.sum().sum().s() / (Ds.cols * (Ds.cols - 1));
            Double avgDr = Dr.sum().sum().s() / (Dr.cols * (Dr.cols - 1));
            Double norm = avgDs / avgDr;
            return Ds.mul(1 - lambda).add(Dr.mul(lambda * norm));
        }
    },
    FREE_ENERGY("FE", Scale.FRACTION_BETA) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            JeigenBuilder db = new JeigenBuilder();
            return db.getDFreeEnergy(A, beta);
        }
    };

    private String name;
    private Scale scale;

    Metric(String name, Scale scale) {
        this.name = name;
        this.scale = scale;
    }

    public static List<Metric> getAll() {
        return Arrays.asList(Metric.values());
    }

    public static List<Metric> getDefaultDistances() {
        return Arrays.asList(
                Metric.COMM,
                Metric.LOG_COMM,
                Metric.SP_CT,
                Metric.FREE_ENERGY,
                Metric.WALK,
                Metric.LOG_FOREST,
                Metric.FOREST,
                Metric.PLAIN_WALK
        );
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public abstract DenseMatrix getD(DenseMatrix A, double t);

    public NodesDistanceDTO getBiggestDistance(DenseMatrix A, double t) {
        NodesDistanceDTO p = new NodesDistanceDTO(0, 0, 0);
        double[][] D = MatrixUtils.toArray2(getD(A, t));
        for (int i = 0; i < D.length; i++) {
            for (int j = 0; j < D[i].length; j++) {
                if (p.getDistance() < D[i][j]) {
                    p = new NodesDistanceDTO(i, j, D[i][j]);
                }
            }
        }
        return p;
    }
}
