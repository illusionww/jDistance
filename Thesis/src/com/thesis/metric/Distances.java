package com.thesis.metric;

import com.thesis.algorithm.johnsons.JohnsonsAlgorithm;
import com.thesis.utils.MatrixUtils;
import com.thesis.utils.PrintUtils;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;
import org.jblas.Solve;

public enum Distances {

    //TODO: Check t < ρ and so on
    WALK("Walk distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            FloatMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix L = MatrixUtils.getL(A);
            FloatMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            FloatMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix L = MatrixUtils.getL(A);
            FloatMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            FloatMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix H = DistancesBuilder.getH0Walk(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix H = DistancesBuilder.getH0Communicability(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float t) {
            FloatMatrix H0 = DistancesBuilder.getH0Communicability(A, t);
            FloatMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float lambda) {
            FloatMatrix L = MatrixUtils.getL(A);
            FloatMatrix Ds = DistancesBuilder.getDShortestPath(A);
            PrintUtils.printArray(Ds, "Shortest path");
            FloatMatrix H = DistancesBuilder.getHResistance(L);
            FloatMatrix Dr = DistancesBuilder.getD(H);
            return Ds.mul(1 - lambda).add(Dr.mul(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances") {
        @Override
        public FloatMatrix getD(FloatMatrix A, float beta) {
            return DistancesBuilder.getDFreeEnergy(A, beta);
        }
    };

    private String name;

    Distances(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public FloatMatrix getD(FloatMatrix A, float t) {
        return null;
    }
}
