package com.jdistance.metric;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;

import static com.jdistance.metric.Shortcuts.*;
import static jeigen.Shortcuts.eye;

public enum Kernel {
    PLAIN_WALK("pWalk", Scale.RHO) { // H0 = (I - tA)^{-1}
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
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
    FOREST("For", Scale.FRACTION) { // H0 = (I + tL)^{-1}
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
            DenseMatrix L = getL(A);

            int d = L.cols;
            DenseMatrix I = eye(d);
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
    COMM("Comm", Scale.FRACTION) { // H0 = exp(tA)

        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
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
    HEAT("Heat", Scale.FRACTION) { // H0 = exp(-tL)
        @Override
        public DenseMatrix getH(DenseMatrix A, double t) {
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

    public static List<Kernel> getAll() {
        return Arrays.asList(Kernel.values());
    }

    public static List<KernelWrapper> getDefaultKernels() {
        return Arrays.asList(
                new KernelWrapper(Kernel.PLAIN_WALK),
                new KernelWrapper(Kernel.WALK),
                new KernelWrapper(Kernel.FOREST),
                new KernelWrapper(Kernel.LOG_FOREST),
                new KernelWrapper(Kernel.COMM),
                new KernelWrapper(Kernel.LOG_COMM),
                new KernelWrapper(Kernel.HEAT),
                new KernelWrapper(Kernel.LOG_HEAT),
                new KernelWrapper(Kernel.RSP),
                new KernelWrapper(Kernel.FE),
                new KernelWrapper(Kernel.SP_CT)
        );
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public abstract DenseMatrix getH(DenseMatrix A, double t);
}
