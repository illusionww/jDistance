package jeigen;

public class ComplexDenseMatrix extends DenseMatrixComplex {
    public ComplexDenseMatrix(DenseMatrixComplex A) {
        super(A.real, A.imag);
    }
}
