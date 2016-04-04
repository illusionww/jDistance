package com.jdistance.metric;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;

public enum Kernel {
    PLAIN_WALK("pWalk", Metric.PLAIN_WALK),
    WALK("Walk", Metric.WALK),
    FOREST("For", Metric.FOREST),
    LOG_FOREST("logFor", Metric.LOG_FOREST),
    COMM30("Comm", Metric.COMM30),
    LOG_COMM30("logComm", Metric.LOG_COMM30),
    HEAT_FAIR("Heat", Metric.HEAT_FAIR),
    LOG_HEAT_FAIR("logHeat", Metric.LOG_HEAT_FAIR),
    FREE_ENERGY("FE", Metric.FREE_ENERGY),
    RSP("RSP", Metric.RSP),
    SP_CT("SP-CT", Metric.SP_CT);

    private String name;
    private Metric metric;

    Kernel(String name, Metric metric) {
        this.name = name;
        this.metric = metric;
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
                new KernelWrapper(Kernel.COMM30),
                new KernelWrapper(Kernel.LOG_COMM30),
                new KernelWrapper(Kernel.HEAT_FAIR),
                new KernelWrapper(Kernel.LOG_HEAT_FAIR),
                new KernelWrapper(Kernel.RSP),
                new KernelWrapper(Kernel.FREE_ENERGY),
                new KernelWrapper(Kernel.SP_CT)
        );
    }

    public String getName() {
        return name;
    }

    public Metric getMetric() {
        return metric;
    }

    public DenseMatrix getK(DenseMatrix A, double t) {
        DenseMatrix D = metric.getD(A, t);
        return makeK(D);
    }

    private DenseMatrix makeK(DenseMatrix D) {
        int size = D.rows;
        DenseMatrix H = DenseMatrix.eye(size).sub(DenseMatrix.ones(size, size).div(size));
        return H.mmul(D.mul(D)).mmul(H).mul(0.5);
    }
}
