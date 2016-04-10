package com.jdistance.metric;

import com.jdistance.utils.MatrixUtils;
import com.jdistance.utils.NodesDistanceDTO;
import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Metric {
    PLAIN_WALK("pWalk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.PLAIN_WALK.getH(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    WALK("Walk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.WALK.getH(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    FOREST("For", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.FOREST.getH(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    LOG_FOREST("logFor", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_FOREST.getH(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    COMM("Comm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.COMM.getH(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    LOG_COMM("logComm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_COMM.getH(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    HEAT("Heat", Scale.FRACTION) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.HEAT.getH(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    LOG_HEAT("logHeat", Scale.FRACTION) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_HEAT.getH(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    RSP("RSP", Scale.FRACTION_REVERSED) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return Shortcuts.getD_RSP(A, beta);
        }
    },
    FE("FE", Scale.FRACTION_REVERSED) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return Shortcuts.getD_FE(A, beta);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR) {
        public DenseMatrix getD(DenseMatrix A, double lambda) {
            DenseMatrix Ds = Shortcuts.getD_ShortestPath(A);
            Ds = Shortcuts.normalize(Ds);

            DenseMatrix L = Shortcuts.getL(A);
            DenseMatrix H = Shortcuts.getH_Resistance(L);
            DenseMatrix Dr = Shortcuts.HtoD(H);
            Dr = Shortcuts.normalize(Dr);

            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    };

    private String name;
    private Scale scale;

    Metric(String name, Scale scale) {
        this.name = name;
        this.scale = scale;
    }

    public static List<MetricWrapper> getAll() {
        return Arrays.asList(Metric.values()).stream().map(MetricWrapper::new).collect(Collectors.toList());
    }

    public static List<MetricWrapper> getDefaultDistances() {
        return Arrays.asList(
                PLAIN_WALK, WALK, FOREST, LOG_FOREST, COMM, LOG_COMM, HEAT, LOG_HEAT, RSP, FE, SP_CT
        ).stream().map(MetricWrapper::new).collect(Collectors.toList());
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
