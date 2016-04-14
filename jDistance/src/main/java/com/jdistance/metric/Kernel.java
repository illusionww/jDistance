package com.jdistance.metric;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.jdistance.metric.Shortcuts.*;
import static jeigen.Shortcuts.eye;

public enum Kernel {
    PLAIN_WALK("pWalk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix ins = I.sub(A.mul(t));
            return pinv(ins);
        }
    },
    WALK("Walk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix H0 = PLAIN_WALK.getH(A, t);
            return H0toH(H0);
        }
    },
    FOREST("For", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            int d = A.cols;
            DenseMatrix I = eye(d);
            DenseMatrix L = getL(A);
            DenseMatrix ins = I.add(L.mul(t));
            return pinv(ins);
        }
    },
    LOG_FOREST("logFor", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix H0 = FOREST.getH(A, t);
            return H0toH(H0);
        }
    },
    COMM("Comm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(tA)
            return A.mul(t).mexp();
        }
    },
    LOG_COMM("logComm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix H0 = COMM.getH(A, t);
            return H0toH(H0);
        }
    },
    HEAT("Heat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix L = getL(A);
            return L.mul(-t).mexp();
        }
    },
    LOG_HEAT("logHeat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix H0 = HEAT.getH(A, t);
            return H0toH(H0);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double lambda) {
            DenseMatrix D = getD_ShortestPath(A);
            DenseMatrix Hs = DtoK(D);
            Hs = normalize(Hs);

            DenseMatrix L = getL(A);
            DenseMatrix Hr = getH_Resistance(L);
            Hr = normalize(Hr);

            return Hs.mul(1 - lambda).add(Hr.mul(lambda));
        }
    },
    PLAIN_WALK_FROM_METRIC("pWalk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            DenseMatrix D = Metric.PLAIN_WALK.getD(A, t);
            return DtoK(D);
        }
    },
    WALK_FROM_METRIC("Walk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.WALK.getD(A, t);
            return DtoK(D);
        }
    },
    FOREST_FROM_METRIC("For", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            DenseMatrix D = Metric.FOREST.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_FOREST_FROM_METRIC("logFor", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_FOREST.getD(A, t);
            return DtoK(D);
        }
    },
    COMM_FROM_METRIC("Comm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(tA)
            DenseMatrix D = Metric.COMM.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_COMM_FROM_METRIC("logComm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_COMM.getD(A, t);
            return DtoK(D);
        }
    },
    HEAT_FROM_METRIC("Heat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix D = Metric.HEAT.getD(A, t);
            return DtoK(D);
        }
    },
    LOG_HEAT_FROM_METRIC("logHeat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_HEAT.getD(A, t);
            return DtoK(D);
        }
    },
    RSP_FROM_METRIC("RSP", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.RSP.getD(A, t);
            return DtoK(distance);
        }
    },
    FE_FROM_METRIC("FE", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.FE.getD(A, t);
            return DtoK(distance);
        }
    },
    SP_CT_FROM_METRIC("SP-CT", Scale.LINEAR) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double lambda) {
            DenseMatrix D = Metric.SP_CT.getD(A, lambda);
            return DtoK(D);
        }
    },
    PLAIN_WALK_FROM_METRIC_SQUARED("pWalk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I - tA)^{-1}
            DenseMatrix D = Metric.PLAIN_WALK.getD(A, t);
            return DtoK_squared(D);
        }
    },
    WALK_FROM_METRIC_SQUARED("Walk", Scale.RHO) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.WALK.getD(A, t);
            return DtoK_squared(D);
        }
    },
    FOREST_FROM_METRIC_SQUARED("For", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = (I + tL)^{-1}
            DenseMatrix D = Metric.FOREST.getD(A, t);
            return DtoK_squared(D);
        }
    },
    LOG_FOREST_FROM_METRIC_SQUARED("logFor", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_FOREST.getD(A, t);
            return DtoK_squared(D);
        }
    },
    COMM_FROM_METRIC_SQUARED("Comm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(tA)
            DenseMatrix D = Metric.COMM.getD(A, t);
            return DtoK_squared(D);
        }
    },
    LOG_COMM_FROM_METRIC_SQUARED("logComm", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_COMM.getD(A, t);
            return DtoK_squared(D);
        }
    },
    HEAT_FROM_METRIC_SQUARED("Heat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) { // H0 = exp(-tL)
            DenseMatrix D = Metric.HEAT.getD(A, t);
            return DtoK_squared(D);
        }
    },
    LOG_HEAT_FROM_METRIC_SQUARED("logHeat", Scale.FRACTION) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix D = Metric.LOG_HEAT.getD(A, t);
            return DtoK_squared(D);
        }
    },
    RSP_FROM_METRIC_SQUARED("RSP", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.RSP.getD(A, t);
            return DtoK_squared(distance);
        }
    },
    FE_FROM_METRIC_SQUARED("FE", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.FE.getD(A, t);
            return DtoK_squared(distance);
        }
    },
    SP_CT_FROM_METRIC_SQUARED("SP-CT", Scale.LINEAR) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double lambda) {
            DenseMatrix D = Metric.SP_CT.getD(A, lambda);
            return DtoK_squared(D);
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

    public static List<KernelWrapper> getDefaultKernels() {
        return Arrays.asList(
                PLAIN_WALK_FROM_METRIC, WALK_FROM_METRIC, FOREST_FROM_METRIC, LOG_FOREST_FROM_METRIC, COMM_FROM_METRIC,
                LOG_COMM_FROM_METRIC, HEAT_FROM_METRIC, LOG_HEAT_FROM_METRIC, RSP_FROM_METRIC, FE_FROM_METRIC, SP_CT_FROM_METRIC
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public static List<KernelWrapper> getDefaultSquaredKernels() {
        return Arrays.asList(
                PLAIN_WALK_FROM_METRIC_SQUARED, WALK_FROM_METRIC_SQUARED, FOREST_FROM_METRIC_SQUARED, LOG_FOREST_FROM_METRIC_SQUARED, COMM_FROM_METRIC_SQUARED,
                LOG_COMM_FROM_METRIC_SQUARED, HEAT_FROM_METRIC_SQUARED, LOG_HEAT_FROM_METRIC_SQUARED, RSP_FROM_METRIC_SQUARED, FE_FROM_METRIC_SQUARED, SP_CT_FROM_METRIC_SQUARED
        ).stream().map(KernelWrapper::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public abstract DenseMatrix getH(DenseMatrix A, double t);
}
