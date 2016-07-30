package jeigen;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

public class BigDecimalMatrix implements Serializable {
    public final int rows;
    public final int cols;
    BigDecimal[] values;

    public BigDecimalMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = new BigDecimal[rows * cols];
    }

    public BigDecimalMatrix(DenseMatrix src) {
        this.rows = src.rows;
        this.cols = src.cols;
        this.values = new BigDecimal[this.rows * this.cols];
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            this.values[i] = BigDecimal.valueOf(src.values[i]);
        }
    }

    public static BigDecimalMatrix zeros(int rows, int cols) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; ++i) {
            result.values[i] = BigDecimal.ZERO;
        }
        return result;
    }

    public static BigDecimalMatrix eye(int size) {
        BigDecimalMatrix result = zeros(size, size);
        for (int i = 0; i < size; ++i) {
            result.values[size * i + i] = BigDecimal.ONE;
        }
        return result;
    }

    public final void set(int row, int col, BigDecimal value) {
        this.values[this.rows * col + row] = value;
    }

    public final void set(int offset, BigDecimal value) {
        this.values[offset] = value;
    }

    public final BigDecimal get(int row, int col) {
        return this.values[this.rows * col + row];
    }

    public BigDecimalMatrix mul(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;
        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i].multiply(scalar);
        }
        return result;
    }

    public BigDecimalMatrix div(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;
        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i].divide(scalar, MathContext.DECIMAL128);
        }
        return result;
    }

    public BigDecimalMatrix add(BigDecimalMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            BigDecimalMatrix result = new BigDecimalMatrix(this.rows, this.cols);
            int capacity = this.rows * this.cols;
            for (int i = 0; i < capacity; ++i) {
                result.values[i] = this.values[i].add(second.values[i]);
            }
            return result;
        } else {
            throw new RuntimeException("matrix size mismatch: " + this.shape() + " vs " + second.shape());
        }
    }

    public BigDecimalMatrix mmul(BigDecimalMatrix second) {
        if (this.cols != second.rows) {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        } else {
            BigDecimalMatrix result = zeros(this.rows, second.cols);
            for (int i = 0; i < this.rows; i++) { // aRow
                for (int j = 0; j < second.cols; j++) { // bColumn
                    for (int k = 0; k < this.cols; k++) { // aColumn
                        result.set(i, j, result.get(i, j).add(this.get(i, k).multiply(second.get(k, j))));
                    }
                }
            }
            return result;
        }
    }

    public DenseMatrix shape() {
        return new DenseMatrix(new double[][]{{(double) this.rows, (double) this.cols}});
    }

    public DenseMatrix toDenseMatrix() {
        BigDecimal maxElement = values[0];
        for (BigDecimal item : values) {
            if (item.compareTo(maxElement) > 0) {
                maxElement = item;
            }
        }
        div(maxElement);
        DenseMatrix matrix = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;
        for (int i = 0; i < numElements; ++i) {
            matrix.values[i] = this.values[i].doubleValue();
        }
        return matrix;
    }
}
