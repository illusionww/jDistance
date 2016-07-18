package com.jdistance.learning.measure;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jdistance.learning.measure.helpers.Shortcuts.*;

public enum Distance {
    P_WALK("pWalk", Scale.RHO, Kernel.P_WALK_H, false),
    WALK("Walk", Scale.RHO, Kernel.WALK_H, false),
    FOR("For", Scale.FRACTION, Kernel.FOR_H, false),
    LOG_FOR("logFor", Scale.FRACTION, Kernel.LOG_FOR_H, false),
    COMM("Comm", Scale.FRACTION, Kernel.COMM_H, true),
    LOG_COMM("logComm", Scale.FRACTION, Kernel.LOG_COMM_H, true),
    HEAT("Heat", Scale.FRACTION, Kernel.HEAT_H, true),
    LOG_HEAT("logHeat", Scale.FRACTION, Kernel.LOG_HEAT_H, true),
    SCT("SCT", Scale.FRACTION, Kernel.SCT_H, false),
    SCCT("SCCT", Scale.FRACTION, Kernel.SCCT_H, false),
    RSP("RSP", Scale.FRACTION_REVERSED, null, false) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return getD_RSP(A, beta);
        }
    },
    FE("FE", Scale.FRACTION_REVERSED, null, false) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return getD_FE(A, beta);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR, null, false) {
        public DenseMatrix getD(DenseMatrix A, double lambda) {
            DenseMatrix Ds = normalize(getD_SP(A));
            DenseMatrix Dr = normalize(HtoD(getH_R(A)));
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    };

    private String name;
    private Scale scale;
    private Kernel parentKernel;
    private Boolean takeSqrt;

    Distance(String name, Scale scale, Kernel parentKernel, Boolean takeSqrt) {
        this.name = name;
        this.scale = scale;
        this.parentKernel = parentKernel;
        this.takeSqrt = takeSqrt;
    }

    public static List<DistanceWrapper> getAll() {
        return Arrays.stream(Distance.values()).map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public static List<DistanceWrapper> getDefaultDistances() {
        return Stream.of(
                P_WALK, WALK, FOR, LOG_FOR, COMM, LOG_COMM, HEAT, LOG_HEAT, RSP, FE, SP_CT
        ).map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public DenseMatrix getD(DenseMatrix A, double t) {
        DenseMatrix H = parentKernel.getK(A, t);
        DenseMatrix D = HtoD(H);
        return takeSqrt ? sqrt(D) : D;
    }

    public Kernel getParentKernel() {
        return parentKernel;
    }
}
