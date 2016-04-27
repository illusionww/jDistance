package com.jdistance.distance;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Distance {
    P_WALK("pWalk", Scale.RHO, Kernel.P_WALK_H),
    WALK("Walk", Scale.RHO, Kernel.WALK_H),
    FOR("For", Scale.FRACTION, Kernel.FOR_H),
    LOG_FOR("logFor", Scale.FRACTION, Kernel.LOG_FOR_H),
    COMM("Comm", Scale.FRACTION, Kernel.COMM_H) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix D = super.getD(A, t);
            return D.sqrt();
        }
    },
    LOG_COMM("logComm", Scale.FRACTION, Kernel.LOG_COMM_H) {
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix D = super.getD(A, t);
            return D.sqrt();
        }
    },
    HEAT("Heat", Scale.FRACTION, Kernel.HEAT_H) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix D = super.getD(A, t);
            return D.sqrt();
        }
    },
    LOG_HEAT("logHeat", Scale.FRACTION, Kernel.LOG_HEAT_H) {
        @Override
        public DenseMatrix getD(DenseMatrix A, double t) {
            DenseMatrix D = super.getD(A, t);
            return D.sqrt();
        }
    },
    RSP("RSP", Scale.FRACTION_REVERSED, null) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return Shortcuts.getD_RSP(A, beta);
        }
    },
    FE("FE", Scale.FRACTION_REVERSED, null) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return Shortcuts.getD_FE(A, beta);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR, null) {

        public DenseMatrix getD(DenseMatrix A, double lambda) {
            DenseMatrix D_SP, D_CP;
            D_SP = calcShortestPath(A);
            D_CP = calcCommuteTime(A);
            return D_SP.mul(1 - lambda).add(D_CP.mul(lambda));
        }

        private DenseMatrix calcShortestPath(DenseMatrix A) {
            DenseMatrix Ds = Shortcuts.getD_ShortestPath(A);
            return Ds;
        }

        private DenseMatrix calcCommuteTime(DenseMatrix A) {
            DenseMatrix L = Shortcuts.getL(A);
            DenseMatrix H = Shortcuts.getH_Resistance(L);
            DenseMatrix Dr = Shortcuts.HtoD(H);
            return Dr;
        }
    };

    private String name;
    private Scale scale;
    private Kernel parentKernel;

    Distance(String name, Scale scale, Kernel parentKernel) {
        this.name = name;
        this.scale = scale;
        this.parentKernel = parentKernel;
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

    public DenseMatrix getD(DenseMatrix A, double t) {
        DenseMatrix H = parentKernel.getK(A, t);
        return Shortcuts.HtoD(H);
    }

    public Kernel getParentKernel() {
        return parentKernel;
    }
}
