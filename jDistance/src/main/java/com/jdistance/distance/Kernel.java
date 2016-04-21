package com.jdistance.distance;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jdistance.distance.Shortcuts.*;
import static jeigen.Shortcuts.eye;

public enum Kernel {
    P_WALK_H("pWalk H", Scale.RHO, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix ins = I.sub(A.mul(t));
            return pinv(ins);
        }
    },
    WALK_H("Walk H", Scale.RHO, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = P_WALK_H.getK(A, t);
            return H0toH(H0);
        }
    },
    FOR_H("For H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix L = getL(A);
            DenseMatrix ins = I.add(L.mul(t));
            return pinv(ins);
        }
    },
    LOG_FOR_H("logFor H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = FOR_H.getK(A, t);
            return H0toH(H0);
        }
    },
    COMM_H("Comm H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(tA)
            return A.mul(t).mexp();
        }
    },
    LOG_COMM_H("logComm H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = COMM_H.getK(A, t);
            return H0toH(H0);
        }
    },
    HEAT_H("Heat H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix L = getL(A);
            return L.mul(-t).mexp();
        }
    },
    LOG_HEAT_H("logHeat H", Scale.FRACTION, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = HEAT_H.getK(A, t);
            return H0toH(H0);
        }
    },
    SP_CT_H("SP-CT H", Scale.LINEAR, null) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double lambda) {
            DenseMatrix D = getD_ShortestPath(A);
            DenseMatrix Hs = DtoK(D);
            Hs = normalize(Hs);

            DenseMatrix L = getL(A);
            DenseMatrix Hr = getH_Resistance(L);
            Hr = normalize(Hr);

            return Hs.mul(1 - lambda).add(Hr.mul(lambda));
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
