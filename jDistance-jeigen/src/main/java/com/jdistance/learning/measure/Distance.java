package com.jdistance.learning.measure;

import jeigen.DenseMatrix;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.jdistance.learning.measure.helpers.Shortcuts.*;

public enum Distance {
    P_WALK("pWalk", Scale.RHO, Kernel.P_WALK_H),
    WALK("Walk", Scale.RHO, Kernel.WALK_H),
    FOR("For", Scale.FRACTION, Kernel.FOR_H),
    LOG_FOR("logFor", Scale.FRACTION, Kernel.LOG_FOR_H),
    COMM("Comm", Scale.FRACTION, Kernel.COMM_H),
    LOG_COMM("logComm", Scale.FRACTION, Kernel.LOG_COMM_H),
    HEAT("Heat", Scale.FRACTION, Kernel.HEAT_H),
    LOG_HEAT("logHeat", Scale.FRACTION, Kernel.LOG_HEAT_H),
    SCT("SCT", Scale.FRACTION, Kernel.SCT_H),
    SCCT("SCCT", Scale.FRACTION, Kernel.SCCT_H),
    RSP("RSP", Scale.FRACTION_REVERSED, null) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return getD_RSP(A, beta);
        }
    },
    FE("FE", Scale.FRACTION_REVERSED, null) {
        public DenseMatrix getD(DenseMatrix A, double beta) {
            return getD_FE(A, beta);
        }
    },
    SP_CT("SP-CT", Scale.LINEAR, null) {
        public DenseMatrix getD(DenseMatrix A, double lambda) {
            DenseMatrix Ds = normalize(getD_SP(A));
            DenseMatrix Dr = normalize(HtoD(getH_R(A)));
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    };

    private String name;
    private Scale scale;
    private Kernel parentKernel;

    Distance(String name, Scale scale, Kernel parentKernel) {
        this.name = name;
        this.scale = scale;
        this.parentKernel = parentKernel;
    }

    public static List<DistanceWrapper> getAll() {
        return Arrays.stream(Distance.values()).map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public static List<DistanceWrapper> getDefaultDistances() {
        return Stream.of(
                P_WALK, WALK, FOR, LOG_FOR, COMM, LOG_COMM, HEAT, LOG_HEAT, RSP, FE, SP_CT
        ).map(DistanceWrapper::new).collect(Collectors.toList());
    }

    public static Distance getByName(String name) {
        for (Distance distance : Distance.values()) {
            if (distance.getName().equals(name)) {
                return distance;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Scale getScale() {
        return scale;
    }

    public DenseMatrix getD(DenseMatrix A, double t) {
        DenseMatrix H = parentKernel.getK(A, t);
        return HtoD(H);
    }

    public Kernel getParentKernel() {
        return parentKernel;
    }
}
