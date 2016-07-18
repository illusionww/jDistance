package com.jdistance.measure;

import jeigen.DenseMatrix;

public class KernelWrapper extends AbstractMeasureWrapper {
    private Kernel kernel;

    public KernelWrapper(Kernel kernel) {
        super(kernel.getName(), kernel.getScale(), true);
        this.kernel = kernel;
    }

    public KernelWrapper(String name, Kernel kernel) {
        super(name, kernel.getScale(), true);
        this.kernel = kernel;
    }

    public KernelWrapper(String name, Scale scale, Kernel kernel) {
        super(name, scale, true);
        this.kernel = kernel;
    }

    @Override
    public DenseMatrix calc(DenseMatrix A, double param) {
        return kernel.getK(A, param);
    }

    public Distance getParentDistance() {
        return kernel.getParentDistance();
    }
}
