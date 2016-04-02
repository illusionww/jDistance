package com.jdistance.metric;

public class KernelWrapper extends AbstractDistanceWrapper {
    private Kernel kernel;

    public KernelWrapper(Kernel kernel) {
        super(kernel.getName(), kernel.getMetric().getScale(), true);
        this.kernel = kernel;
    }

    public KernelWrapper(String name, Kernel kernel) {
        super(name, kernel.getMetric().getScale(), true);
        this.kernel = kernel;
    }

    public KernelWrapper(String name, Scale scale, Kernel kernel) {
        super(name, scale, true);
        this.kernel = kernel;
    }

    public Kernel getKernel() {
        return kernel;
    }
}
