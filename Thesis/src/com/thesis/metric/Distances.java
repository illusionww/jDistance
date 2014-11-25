package com.thesis.metric;

import com.thesis.matrix.CustomUtils;
import com.thesis.matrix.ElementWise;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

public enum Distances {

    //TODO: Check t < ρ and so on
    WALK("Walk distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            RealMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            RealMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_FOREST("Logarithmic Forest Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            RealMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            RealMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_FOREST("[\"Plain\"] Forest Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            RealMatrix H0 = DistancesBuilder.getH0Forest(L, t);
            RealMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    PLAIN_WALK("\"Plain\" Walk Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            RealMatrix H0 = DistancesBuilder.getH0Walk(A, t);
            RealMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    COMMUNICABILITY("Communicability Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            RealMatrix H = DistancesBuilder.getH0Communicability(A, t);
            return DistancesBuilder.getD(H);
        }
    },
    LOGARITHMIC_COMMUNICABILITY("Logarithmic Communicability Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
            // TODO: add loops
            RealMatrix H0 = DistancesBuilder.getH0Communicability(A, t);
            RealMatrix H = DistancesBuilder.H0toH(H0);
            return DistancesBuilder.getD(H);
        }
    },
    COMBINATIONS("Convex combination of the shortest path and resistance metrics") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double lambda) {
            // TODO: choose parameters
            RealMatrix Ds = Distances.LOGARITHMIC_FOREST.getD(L, A, 0);
            RealMatrix Dr = Distances.LOGARITHMIC_FOREST.getD(L, A, 10000);
            return Ds.scalarMultiply(1 - lambda).add(Dr.scalarMultiply(lambda));
        }
    },
    HELMHOLTZ_FREE_ENERGY("Helmholtz Free Energy Distances") {
        @Override
        public RealMatrix getD(RealMatrix L, RealMatrix A, double beta) {
            int dimension = A.getColumnDimension();
            RealMatrix Pref = ElementWise.Pref(A);
            RealMatrix C = ElementWise.inverseElements(A);
            RealMatrix W = Pref.multiply(ElementWise.exp(C.scalarMultiply(-beta)));
            RealMatrix I = MatrixUtils.createRealIdentityMatrix(dimension);
            RealMatrix Z = MatrixUtils.inverse(I.subtract(W));
            RealMatrix Zh = Z.multiply(CustomUtils.getDiag(Z));
            RealMatrix F = ElementWise.log(Zh).scalarMultiply(-1/beta);
            return F.add(F.transpose()).scalarMultiply(0.5);
        }
    };

    private String name;

    Distances(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RealMatrix getD(RealMatrix L, RealMatrix A, double t) {
        return null;
    }
}
