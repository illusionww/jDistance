package com.thesis.utils;

import jeigen.DenseMatrix;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;

public class BigDecimalMatrix {
    /**
     * Number of rows
     */
    public final int rows;
    /**
     * Number of columns
     */
    public final int cols;
    /**
     * underlying array of values, in column-major, dense format
     */
    BigDecimal[] values;

    public BigDecimalMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = new BigDecimal[rows * cols];
    }

    /**
     * Creates matrix from valuesstring in format "12 3; 4 5"
     * Result:
     * 12 3
     * 4  5
     */
    public BigDecimalMatrix(String valuesstring) {
        String[] lines = valuesstring.split(";");
        rows = lines.length;
        int row = 0;
        if (rows == 0) {
            cols = 0;
            return;
        }
        String firstline = lines[0];
        String newmodifiedline = firstline.replace("  ", " ").trim();
        while (!newmodifiedline.equals(firstline)) {
            firstline = newmodifiedline;
            newmodifiedline = firstline.replace("  ", " ").trim();
        }
        cols = firstline.split(" ").length;
        values = new BigDecimal[rows * cols];
        for (String line : lines) {
            newmodifiedline = line.replace("  ", " ").trim();
            while (!newmodifiedline.equals(line)) {
                line = newmodifiedline;
                newmodifiedline = line.replace("  ", " ").trim();
            }
            String[] splitline = line.split(" ");
            if (splitline.length != cols) {
                throw new RuntimeException("Unequal sized rows in " + valuesstring);
            }
            for (int col = 0; col < cols; col++) {
                set(row, col, new BigDecimal(splitline[col]));
            }
            row++;
        }
    }

    public BigDecimalMatrix(BigDecimalMatrix src) {
        this.rows = src.rows;
        this.cols = src.cols;
        values = new BigDecimal[rows * cols];
        int numElements = this.rows * this.cols;
        for (int i = 0; i < numElements; i++) {
            this.values[i] = src.values[i];
        }
    }

    /**
     * constructs new dense matrix from values
     */
    public BigDecimalMatrix(BigDecimal[][] values) {
        this.rows = values.length;
        this.cols = values[0].length;
        this.values = new BigDecimal[rows * cols];
        int i = 0;
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                this.values[i] = values[r][c];
                i++;
            }
        }
    }

    public BigDecimalMatrix(double[][] values) {
        this.rows = values.length;
        this.cols = values[0].length;
        this.values = new BigDecimal[rows * cols];
        int i = 0;
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                this.values[i] = new BigDecimal(values[r][c]);
                i++;
            }
        }
    }

    /**
     * return rows*cols dense matrix of zeros
     */
    public static BigDecimalMatrix zeros(int rows, int cols) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = BigDecimal.ZERO;
        }
        return result;
    }

    /**
     * return rows*cols dense matrix of ones
     */
    public static BigDecimalMatrix ones(int rows, int cols) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = BigDecimal.ONE;
        }
        return result;
    }

    /**
     * return identity matrix of size 'size', as dense matrix
     */
    public static BigDecimalMatrix eye(int size) {
        BigDecimalMatrix result = zeros(size, size);
        for (int i = 0; i < size; i++) {
            result.values[size * i + i] = BigDecimal.ONE;
        }
        return result;
    }

    /**
     * returns matrix with v along the diagonal
     * v should have a single column
     */
    public static BigDecimalMatrix diag(BigDecimalMatrix v) {
        if (v.cols != 1) {
            throw new RuntimeException("diag needs a matrix with one column exactly");
        }
        int size = v.rows;
        BigDecimalMatrix result = new BigDecimalMatrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, v.get(i, 0));
        }
        return result;
    }

    /**
     * Return value at positiono (0,0)
     */
    public BigDecimal s() {
        return values[0];
    }

    /**
     * return copy of column col
     */
    public BigDecimalMatrix col(int col) {
        return slice(0, rows, col, col + 1);
    }

    /**
     * return copy of row row
     */
    public BigDecimalMatrix row(int row) {
        return slice(row, row + 1, 0, cols);
    }

    /**
     * return copy of columns from startcol to (endcolexclusive-1)
     */
    public BigDecimalMatrix cols(int startcol, int endcolexclusive) {
        return slice(0, rows, startcol, endcolexclusive);
    }

    /**
     * return copy of rows from startrow to (endrowexclusive-1)
     */
    public BigDecimalMatrix rows(int startrow, int endrowexclusive) {
        return slice(startrow, endrowexclusive, 0, cols);
    }

    /**
     * return copy of matrix from startrow to (endrowexclusive-1)
     * and startcol to (endcolexclusive-1)
     */
    public BigDecimalMatrix slice(int startrow, int endrowexclusive, int startcol, int endcolexclusive) {
        int resultrows = endrowexclusive - startrow;
        int resultcols = endcolexclusive - startcol;
        if (endrowexclusive > rows) {
            throw new RuntimeException("endrow must not exceed rows " + endrowexclusive + " vs " + rows);
        }
        if (endcolexclusive > cols) {
            throw new RuntimeException("endcol must not exceed cols " + endcolexclusive + " vs " + cols);
        }
        if (startrow < 0) {
            throw new RuntimeException("startrow must be at least 0, but was  " + startrow);
        }
        if (startcol < 0) {
            throw new RuntimeException("startcol must be at least 0, but was  " + startcol);
        }
        BigDecimalMatrix result = new BigDecimalMatrix(resultrows, resultcols);
        for (int c = 0; c < resultcols; c++) {
            int resultoffset = resultrows * c;
            int sourceoffset = (startcol + c) * rows;
            for (int r = 0; r < resultrows; r++) {
                result.values[resultoffset + r] = values[sourceoffset + startrow + r];
            }
        }
        return result;
    }

    /**
     * returns matrix with this matrix along the diagonal
     * this matrix should have a single column
     */
    public BigDecimalMatrix diag() {
        if (cols != 1) {
            throw new RuntimeException("diag needs a matrix with one column exactly");
        }
        int size = rows;
        BigDecimalMatrix result = new BigDecimalMatrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, get(i, 0));
        }
        return result;
    }

    /**
     * returns the sum over rows, or if only one row, returns
     * sum over columns
     */
    public BigDecimalMatrix sum() {
        if (rows > 1) {
            return sumOverRows();
        }
        return sumOverCols();
    }

    /**
     * sum aggregate over rows
     * result has a single row,
     * and the same columns as the input
     * matrix.
     */
    public BigDecimalMatrix sumOverRows() {
        BigDecimalMatrix result = new BigDecimalMatrix(1, cols);
        for (int c = 0; c < cols; c++) {
            int offset = c * rows;
            BigDecimal sum = BigDecimal.ZERO;
            for (int r = 0; r < rows; r++) {
                sum = sum.add(values[offset + r]);
            }
            result.set(0, c, sum);
        }
        return result;
    }

    public BigDecimalMatrix sumOverCols() {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, 1);
        for (int r = 0; r < rows; r++) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int c = 0; c < cols; c++) {
                sum = sum.add(get(r, c));
            }
            result.set(r, 0, sum);
        }
        return result;
    }

    public BigDecimalMatrix maxOverRows() {
        if (cols < 1) {
            throw new RuntimeException("maxoverrows can't be called on empty matrix");
        }
        BigDecimalMatrix result = new BigDecimalMatrix(1, cols);
        for (int c = 0; c < cols; c++) {
            int offset = c * rows;
            BigDecimal max = get(0, c);
            for (int r = 0; r < rows; r++) {
                max = max.max(values[offset + r]);
            }
            result.set(0, c, max);
        }
        return result;
    }

    public BigDecimalMatrix maxOverCols() {
        if (rows < 1) {
            throw new RuntimeException("maxOverCols can't be called on empty matrix");
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, 1);
        for (int r = 0; r < rows; r++) {
            BigDecimal max = get(r, 0);
            for (int c = 0; c < cols; c++) {
                max = max.max(get(r, c));
            }
            result.set(r, 0, max);
        }
        return result;
    }

    public BigDecimalMatrix minOverRows() {
        if (cols < 1) {
            throw new RuntimeException("minoverrows can't be called on empty matrix");
        }
        BigDecimalMatrix result = new BigDecimalMatrix(1, cols);
        for (int c = 0; c < cols; c++) {
            int offset = c * rows;
            BigDecimal min = get(0, c);
            for (int r = 0; r < rows; r++) {
                min = min.min(values[offset + r]);
            }
            result.set(0, c, min);
        }
        return result;
    }

    public BigDecimalMatrix minOverCols() {
        if (rows < 1) {
            throw new RuntimeException("minOverCols can't be called on empty matrix");
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, 1);
        for (int r = 0; r < rows; r++) {
            BigDecimal min = get(r, 0);
            for (int c = 0; c < cols; c++) {
                min = min.min(get(r, c));
            }
            result.set(r, 0, min);
        }
        return result;
    }

    /**
     * returns transpose
     */
    public BigDecimalMatrix t() { // this could be optimized a lot, by not actually transposing...
        BigDecimalMatrix result = new BigDecimalMatrix(cols, rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result.set(c, r, get(r, c));
            }
        }
        return result;
    }

    /**
     * sets value of matrix at (row,col) to value
     */
    public final void set(int row, int col, BigDecimal value) {
        values[rows * col + row] = value;
    }

    /**
     * sets value of matrix at (offset % rows,offset / rows) to value
     * less convenient, but faster
     */
    public final void set(int offset, BigDecimal value) {
        values[offset] = value;
    }

    /**
     * gets value of matrix at (row,col)
     */
    public final BigDecimal get(int row, int col) {
        return values[rows * col + row];
    }

    /**
     * gets all values of matrix
     */
    public final BigDecimal[] getValues() {
        return values;
    }

    /**
     * for each element: element = abs( element )
     */
    public BigDecimalMatrix abs() {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].abs();
        }
        return result;
    }

    /**
     * for each element: element = element * scalar
     */

    public BigDecimalMatrix mul(double scalar) {
        return mul(new BigDecimal(scalar));
    }

    public BigDecimalMatrix mul(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].multiply(scalar);
        }
        return result;
    }

    /**
     * for each element: element = Math.pow(element,power)
     */
    public BigDecimalMatrix pow(BigDecimal power) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = BigDecimalMath.pow(values[i], power);
        }
        return result;
    }

    /**
     * for each element: element = Math.pow(element,power)
     */
    public BigDecimalMatrix sqrt() {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = BigDecimalMath.sqrt(values[i]);
        }
        return result;
    }

    /**
     * for each element: element = element / scalar
     */

    public BigDecimalMatrix div(double scalar) {
        return div(new BigDecimal(scalar));
    }

    public BigDecimalMatrix div(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].divide(scalar);
        }
        return result;
    }

    /**
     * for each element: element = element + scalar
     */
    public BigDecimalMatrix add(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].add(scalar);
        }
        return result;
    }

    /**
     * for each element: element = element - scalar
     */
    public BigDecimalMatrix sub(BigDecimal scalar) {
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].subtract(scalar);
        }
        return result;
    }

    /**
     * for each element: element[result] = element[this] * element[second]
     */
    public BigDecimalMatrix mul(BigDecimalMatrix second) {
        if (this.cols != second.cols || this.rows != second.rows) {
            throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape());
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].multiply(second.values[i]);
        }
        return result;
    }

    /**
     * for each element: element[result] = element[this] / element[second]
     */
    public BigDecimalMatrix div(BigDecimalMatrix second) {
        if (this.cols != second.cols || this.rows != second.rows) {
            throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape());
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].divide(second.values[i]);
        }
        return result;
    }

    /**
     * for each element: element[result] = element[this] + element[second]
     */
    public BigDecimalMatrix add(BigDecimalMatrix second) {
        if (this.cols != second.cols || this.rows != second.rows) {
            throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape());
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].add(second.values[i]);
        }
        return result;
    }

    /**
     * for each element: element[result] = element[this] - element[second]
     */
    public BigDecimalMatrix sub(BigDecimalMatrix second) {
        if (this.cols != second.cols || this.rows != second.rows) {
            throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape());
        }
        BigDecimalMatrix result = new BigDecimalMatrix(rows, cols);
        int capacity = rows * cols;
        for (int i = 0; i < capacity; i++) {
            result.values[i] = values[i].subtract(second.values[i]);
        }
        return result;
    }

    /**
     * checks whether the sizes and values of this and osecond are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BigDecimalMatrix)) {
            return false;
        }
        BigDecimalMatrix m2 = (BigDecimalMatrix) obj;
        if (this.rows != m2.rows || this.cols != m2.cols) {
            return false;
        }
        for (int i = 0; i < this.rows * this.cols; i++) {
            if (this.getValues()[i].doubleValue() != m2.getValues()[i].doubleValue()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(DenseMatrix second) {
        if (this.cols != second.cols || this.rows != second.rows) {
            throw new RuntimeException("matrix size mismatch: " + shape() + " vs " + second.shape());
        }
        for (int i = 0; i < cols*rows; i++) {
            if (this.getValues()[i].doubleValue() != second.getValues()[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * matrix multiplication of this by second
     */
    public BigDecimalMatrix mmul(BigDecimalMatrix second) {
        if (this.cols != second.rows) {
            throw new RuntimeException("matrix size mismatch " + shape() + " vs " + second.shape());
        }

        BigDecimalMatrix res = new BigDecimalMatrix(this.rows, second.cols);

        for (int i = 0; i < this.cols; i++) {
            for (int j = 0; j < second.rows; j++) {
                res.set(i, j, BigDecimal.ZERO);
                for (int k = 0; k < this.rows; k++) {
                     res.set(i, j, res.get(i, j).add(this.get(i, k).multiply(second.get(k, j))));
                }
            }
        }
        return res;
    }

    /**
     * returns matrix with number of rows and columns of this
     */
    public BigDecimalMatrix shape() {
        return new BigDecimalMatrix(new BigDecimal[][]{{new BigDecimal(rows), new BigDecimal(cols)}});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("BigDecimalMatrix, " + rows + " * " + cols + ":\n");
        stringBuilder.append("\n");
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                stringBuilder.append(get(r, c));
                stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public BigDecimalMatrix mexp(int nSteps) {
        if (this.cols != this.rows) {
            throw new RuntimeException("exp matrix size error: must be square matrix");
        }

        BigDecimalMatrix runtot = eye(this.rows);
        BigDecimalMatrix sum = eye(this.rows);

        double factorial = 1.0;
        for (int i = 1; i <= nSteps; i++) {
            factorial /= (double) i;
            sum = sum.mmul(this);
            runtot = runtot.add(sum.mul(new BigDecimal(factorial)));
        }
        return runtot;
    }

    public double[][] getDoubleArray() {
        double[][] array = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[j][i] = get(i, j).doubleValue();
            }
        }
        return array;
    }
}
