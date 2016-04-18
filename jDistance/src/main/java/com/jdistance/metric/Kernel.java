package com.jdistance.metric;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jdistance.metric.Shortcuts.*;
import static jeigen.Shortcuts.eye;

public enum Kernel {
    P_WALK_H("pWalk H", Scale.RHO) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix ins = I.sub(A.mul(t));
            return pinv(ins);
        }
    },
    WALK_H("Walk H", Scale.RHO) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = P_WALK_H.getK(A, t);
            return H0toH(H0);
        }
    },
    FOR_H("For H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix L = getL(A);
            DenseMatrix ins = I.add(L.mul(t));
            return pinv(ins);
        }
    },
    LOG_FOR_H("logFor H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = FOR_H.getK(A, t);
            return H0toH(H0);
        }
    },
    COMM_H("Comm H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(tA)
            return A.mul(t).mexp();
        }
    },
    LOG_COMM_H("logComm H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = COMM_H.getK(A, t);
            return H0toH(H0);
        }
    },
    HEAT_H("Heat H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix L = getL(A);
            return L.mul(-t).mexp();
        }
    },
    LOG_HEAT_H("logHeat H", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix H0 = HEAT_H.getK(A, t);
            return H0toH(H0);
        }
    },
    SP_CT_H("SP-CT H", Scale.LINEAR) {
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
    P_WALK_K("pWalk K", Scale.RHO) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            DenseMatrix D = Metric.P_WALK.getD(A, t);
            return DtoK(D);
        }
    },
    WALK_K("Walk K", Scale.RHO) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix D = Metric.WALK.getD(A, t);
            return DtoK(D);
        }
    },
    FOR_K("For K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            DenseMatrix D = Metric.FOR.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_FOR_K("logFor K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_FOR.getD(A, t);
            return DtoK(D);
        }
    },
    COMM_K("Comm K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(tA)
            DenseMatrix D = Metric.COMM.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_COMM_K("logComm K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_COMM.getD(A, t);
            return DtoK(D);
        }
    },
    HEAT_K("Heat K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix D = Metric.HEAT.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_HEAT_K("logHeat K", Scale.FRACTION) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_HEAT.getD(A, t);
            return DtoK(D);
        }
    },
    RSP_K("RSP K", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.RSP.getD(A, t);
            return DtoK(distance);
        }
    },
    FE_K("FE K", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.FE.getD(A, t);
            return DtoK(distance);
        }
    },
    SP_CT_K("SP-CT K", Scale.LINEAR) {
        @Override
        public DenseMatrix getK(DenseMatrix A, double lambda) {
            DenseMatrix D = Metric.SP_CT.getD(A, lambda);
            return DtoK(D);
        }
    };

    private String name;
    private Scale scale;

    Kernel(String name, Scale scale) {
        this.name = name;
        this.scale = scale;
    }

    public static List<KernelWrapper> getAll() {
        return Arrays.asList(Kernel.values()).stream().map(KernelWrapper::new).collect(Collectors.toList());
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

    public abstract DenseMatrix getK(DenseMatrix A, double t);
}
