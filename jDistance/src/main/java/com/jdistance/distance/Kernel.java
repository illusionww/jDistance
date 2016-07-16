package com.jdistance.distance;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jdistance.distance.Shortcuts.*;
import static jeigen.Shortcuts.*;

public enum Kernel {
    P_WALK_H("pWalk H", Scale.RHO, null) { // H0 = (I - tA)^{-1}
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return pinv(eye(A.cols).sub(A.mul(t)));
        }
    },
    WALK_H("Walk H", Scale.RHO, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return H0toH(P_WALK_H.getK(A, t));
        }
    },
    FOR_H("For H", Scale.FRACTION, null) { // H0 = (I + tL)^{-1}
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return pinv(eye(A.cols).add(getL(A).mul(t)));
        }
    },
    LOG_FOR_H("logFor H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return H0toH(FOR_H.getK(A, t));
        }
    },
    COMM_H("Comm H", Scale.FRACTION, null) { // H0 = exp(tA)
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return A.mul(t).mexp();
        }
    },
    LOG_COMM_H("logComm H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return H0toH(COMM_H.getK(A, t));
        }
    },
    HEAT_H("Heat H", Scale.FRACTION, null) { // H0 = exp(-tL)
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return getL(A).mul(-t).mexp();
        }
    },
    LOG_HEAT_H("logHeat H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            return H0toH(HEAT_H.getK(A, t));
        }
    },
    SP_CT_H("SP-CT H", Scale.LINEAR, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double lambda) {
            DenseMatrix Hs = normalize(DtoK(getD_ShortestPath(A)));
            DenseMatrix Hc = normalize(getH_Resistance(getL(A)));
            return Hs.mul(1 - lambda).add(Hc.mul(lambda));
        }
    },
    SP_CT_NEW_H("SP-CT NEW H", Scale.LINEAR, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double lambda) {
            DenseMatrix Hs = normalize(DtoK(getD_ShortestPath(A)));
            DenseMatrix Hc = normalize(pinv(getL(A)));
            return Hs.mul(1 - lambda).add(Hc.mul(lambda));
        }
    },
    P_WALK_K("pWalk K", Scale.RHO, Distance.P_WALK),
    WALK_K("Walk K", Scale.RHO, Distance.WALK),
    FOR_K("For K", Scale.FRACTION, Distance.FOR),
    LOG_FOR_K("logFor K", Scale.FRACTION, Distance.LOG_FOR),
    COMM_K("Comm K", Scale.FRACTION, Distance.COMM),
    LOG_COMM_K("logComm K", Scale.FRACTION, Distance.LOG_COMM),
    HEAT_K("Heat K", Scale.FRACTION, Distance.HEAT),
    LOG_HEAT_K("logHeat K", Scale.FRACTION, Distance.LOG_HEAT),
    RSP_K("RSP K", Scale.FRACTION_REVERSED, Distance.RSP),
    FE_K("FE K", Scale.FRACTION_REVERSED, Distance.FE),
    SP_CT_K("SP-CT K", Scale.LINEAR, Distance.SP_CT);

    private String name;
    private Scale scale;
    private Distance parentDistance;

    Kernel(String name, Scale scale, Distance parentDistance) {
        this.name = name;
        this.scale = scale;
        this.parentDistance = parentDistance;
    }

    public static List<KernelWrapper> getAll() {
        return Arrays.asList(Kernel.values()).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public static List<KernelWrapper> getDefaultKernels() {
        return Arrays.asList(
                P_WALK_H, WALK_H, FOR_H, LOG_FOR_H, COMM_H,
                LOG_COMM_H, HEAT_H, LOG_HEAT_H, RSP_K, FE_K, SP_CT_H
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public static List<KernelWrapper> getAllH() {
        return Arrays.asList(
                P_WALK_H, WALK_H, FOR_H, LOG_FOR_H, COMM_H,
                LOG_COMM_H, HEAT_H, LOG_HEAT_H, SP_CT_H
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public static List<KernelWrapper> getAllK_exceptRSP_FE() {
        return Arrays.asList(
                P_WALK_K, WALK_K, FOR_K, LOG_FOR_K, COMM_K,
                LOG_COMM_K, HEAT_K, LOG_HEAT_K, SP_CT_K
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public static List<KernelWrapper> getAllK() {
        return Arrays.asList(
                P_WALK_K, WALK_K, FOR_K, LOG_FOR_K, COMM_K,
                LOG_COMM_K, HEAT_K, LOG_HEAT_K, RSP_K, FE_K, SP_CT_K
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public DenseMatrix getK(DenseMatrix A, double t) {
        DenseMatrix distance = parentDistance.getD(A, t);
        return DtoK(distance);
    }

    public Distance getParentDistance() {
        return parentDistance;
    }
}
