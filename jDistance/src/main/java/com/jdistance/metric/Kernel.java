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
    RSP("RSP", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.RSP.getD(A, t);
            return DtoH(distance);
        }
    },
    FE("FE", Scale.FRACTION_REVERSED) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix distance = Metric.FE.getD(A, t);
            return DtoH(distance);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR) {
        @Override
        public DenseMatrix getH(DenseMatrix A, double lambda) {
            DenseMatrix D = Shortcuts.getD_ShortestPath(A);
            DenseMatrix Hs = Shortcuts.DtoH(D);
            Hs = Shortcuts.normalize(Hs);

            DenseMatrix L = Shortcuts.getL(A);
            DenseMatrix Hr = Shortcuts.getH_Resistance(L);
            Hr = Shortcuts.normalize(Hr);

            return Hs.mul(1 - lambda).add(Hr.mul(lambda));
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
                PLAIN_WALK, WALK, FOREST, LOG_FOREST, COMM, LOG_COMM, HEAT, LOG_HEAT, RSP, FE, SP_CT
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
