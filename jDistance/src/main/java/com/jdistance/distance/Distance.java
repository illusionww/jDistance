package com.jdistance.distance;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Distance {
    P_WALK("pWalk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.P_WALK_H.getK(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    WALK("Walk", Scale.RHO) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.WALK_H.getK(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    FOR("For", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.FOR_H.getK(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    LOG_FOR("logFor", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_FOR_H.getK(A, t);
            return Shortcuts.HtoD(H);
        }
    },
    COMM("Comm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.COMM_H.getK(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    LOG_COMM("logComm", Scale.FRACTION) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_COMM_H.getK(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    HEAT("Heat", Scale.FRACTION) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.HEAT_H.getK(A, t);
            DenseMatrix D = Shortcuts.HtoD(H);
            return D.sqrt();
        }
    },
    LOG_HEAT("logHeat", Scale.FRACTION) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix H = Kernel.LOG_HEAT_H.getK(A, t);
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
        private DenseMatrix cachedA = null;
        private DenseMatrix cachedSP;
        private DenseMatrix cachedCT;

        public DenseMatrix getD(DenseMatrix A, double lambda) {
            DenseMatrix D_SP, D_CP;
            if (A == cachedA) {
                D_SP = cachedSP;
                D_CP = cachedCT;
            } else {
                D_SP = calcShortestPath(A);
                D_CP = calcCommuteTime(A);
                cachedA = A;
                cachedSP = D_SP;
                cachedCT = D_CP;
            }
            return D_SP.mul(1 - lambda).add(D_CP.mul(lambda));
        }

        private DenseMatrix calcShortestPath(DenseMatrix A) {
            DenseMatrix Ds = Shortcuts.getD_ShortestPath(A);
            return Shortcuts.normalize(Ds);
        }

        private DenseMatrix calcCommuteTime(DenseMatrix A) {
            DenseMatrix L = Shortcuts.getL(A);
            DenseMatrix H = Shortcuts.getH_Resistance(L);
            DenseMatrix Dr = Shortcuts.HtoD(H);
            return Shortcuts.normalize(Dr);
        }
    };

    private String name;
    private Scale scale;

    Distance(String name, Scale scale) {
        this.name = name;
        this.scale = scale;
    }

    public static List<DistanceWrapper> getAll() {
        return Arrays.asList(Distance.values()).stream().map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public static List<DistanceWrapper> getDefaultDistances() {
        return Arrays.asList(
                P_WALK, WALK, FOR, LOG_FOR, COMM, LOG_COMM, HEAT, LOG_HEAT, RSP, FE, SP_CT
        ).stream().map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public abstract DenseMatrix getD(DenseMatrix A, double t);
}
