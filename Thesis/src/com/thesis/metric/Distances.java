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
            FloatMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            FloatMatrix H = DistancesBuilder.H0toH(H0);
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
            // TODO: add loops
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
            int d = A.getColumns();

            // P^{ref} = D^{-1}*A, D = Diag(A*e)
            FloatMatrix e = FloatMatrix.ones(d);
            FloatMatrix Pref = FloatMatrix.diag(A.mmul(e));
            PrintUtils.printArray(Pref, "Pref");

            // W = P^{ref} *(element-wise) exp (-βC)
            FloatMatrix C = JohnsonsAlgorithm.getAllShortestPaths(A);
            FloatMatrix W = Pref.mul(MatrixFunctions.exp(C.mul(-beta)));
            PrintUtils.printArray(W, "W");

            // Z = (I - W)^{-1}
            FloatMatrix I = FloatMatrix.eye(d);
            FloatMatrix Z = Solve.pinv(I.sub(W));
            PrintUtils.printArray(Z, "Z");

            // Z^h = Z * D_h^{-1}, D_h = Diag(Z)
            FloatMatrix Dh = FloatMatrix.diag(Z.diag());
            FloatMatrix Zh = Z.mul(Solve.pinv(Dh));
            PrintUtils.printArray(Zh, "Zh");

            // Φ = -1/β * log(Z^h)
            FloatMatrix F = MatrixFunctions.log(Zh).div(-beta);
            PrintUtils.printArray(F, "Φ");

            // Δ_FE = (Φ + Φ^T)/2
            return F.add(F.transpose()).div(2);
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
