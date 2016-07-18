package jeigen;

import jeigen.JeigenJna.Jeigen;
import jeigen.statistics.Statistics;

import java.io.Serializable;
import java.util.Random;

public class DenseMatrix implements Serializable {
    public final int rows;
    public final int cols;
    double[] values;

    public DenseMatrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.values = new double[rows * cols];
    }

    public DenseMatrix(String valuesstring) {
        String[] lines = valuesstring.split(";");
        this.rows = lines.length;
        int row = 0;
        if (this.rows == 0) {
            this.cols = 0;
        } else {
            String firstline = lines[0];

            String newmodifiedline;
            for (newmodifiedline = firstline.replace("  ", " ").trim(); !newmodifiedline.equals(firstline); newmodifiedline = newmodifiedline.replace("  ", " ").trim()) {
                firstline = newmodifiedline;
            }

            this.cols = firstline.split(" ").length;
            this.values = new double[this.rows * this.cols];
            String[] arr$ = lines;
            int len$ = lines.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String line = arr$[i$];

                for (newmodifiedline = line.replace("  ", " ").trim(); !newmodifiedline.equals(line); newmodifiedline = newmodifiedline.replace("  ", " ").trim()) {
                    line = newmodifiedline;
                }

                String[] splitline = line.split(" ");
                if (splitline.length != this.cols) {
                    throw new RuntimeException("Unequal sized rows in " + valuesstring);
                }

                for (int col = 0; col < this.cols; ++col) {
                    this.set(row, col, Double.parseDouble(splitline[col]));
                }

                ++row;
            }

        }
    }

    public DenseMatrix(DenseMatrix src) {
        this.rows = src.rows;
        this.cols = src.cols;
        this.values = new double[this.rows * this.cols];
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            this.values[i] = src.values[i];
        }

    }

    public DenseMatrix(double[][] values) {
        this.rows = values.length;
        this.cols = values[0].length;
        this.values = new double[this.rows * this.cols];
        int i = 0;

        for (int c = 0; c < this.cols; ++c) {
            for (int r = 0; r < this.rows; ++r) {
                this.values[i] = values[r][c];
                ++i;
            }
        }

    }

    public static DenseMatrix rand(int rows, int cols) {
        DenseMatrix result = new DenseMatrix(rows, cols);
        Random random = new Random();
        int i = 0;

        for (int c = 0; c < cols; ++c) {
            for (int r = 0; r < rows; ++r) {
                result.values[i] = random.nextDouble();
                ++i;
            }
        }

        return result;
    }

    public static DenseMatrix zeros(int rows, int cols) {
        DenseMatrix result = new DenseMatrix(rows, cols);
        return result;
    }

    public static DenseMatrix ones(int rows, int cols) {
        DenseMatrix result = new DenseMatrix(rows, cols);
        int capacity = rows * cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = 1.0D;
        }

        return result;
    }

    public static DenseMatrix eye(int size) {
        DenseMatrix result = new DenseMatrix(size, size);

        for (int i = 0; i < size; ++i) {
            result.values[size * i + i] = 1.0D;
        }

        return result;
    }

    public static DenseMatrix diag(DenseMatrix v) {
        if (v.cols != 1) {
            throw new RuntimeException("diag needs a matrix with one column exactly");
        } else {
            int size = v.rows;
            DenseMatrix result = new DenseMatrix(size, size);

            for (int i = 0; i < size; ++i) {
                result.set(i, i, v.get(i, 0));
            }

            return result;
        }
    }

    public double s() {
        return this.values[0];
    }

    public DenseMatrix col(int col) {
        return this.slice(0, this.rows, col, col + 1);
    }

    public DenseMatrix row(int row) {
        return this.slice(row, row + 1, 0, this.cols);
    }

    public DenseMatrix cols(int startcol, int endcolexclusive) {
        return this.slice(0, this.rows, startcol, endcolexclusive);
    }

    public DenseMatrix rows(DenseMatrix indexes) {
        if (indexes.cols != 1) {
            throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
        } else {
            int cols = this.cols;
            DenseMatrix result = new DenseMatrix(indexes.rows, cols);

            for (int i = 0; i < indexes.rows; ++i) {
                int srcrow = (int) indexes.get(i, 0);

                for (int c = 0; c < cols; ++c) {
                    result.set(i, c, this.get(srcrow, c));
                }
            }

            return result;
        }
    }

    public DenseMatrix cols(DenseMatrix indexes) {
        if (indexes.cols != 1) {
            throw new RuntimeException("indexes should have one column, but had " + indexes.cols + " columns");
        } else {
            int rows = this.rows;
            DenseMatrix result = new DenseMatrix(rows, indexes.rows);

            for (int i = 0; i < indexes.rows; ++i) {
                int srccol = (int) indexes.get(i, 0);

                for (int r = 0; r < rows; ++r) {
                    result.set(r, i, this.get(r, srccol));
                }
            }

            return result;
        }
    }

    public DenseMatrix rows(int startrow, int endrowexclusive) {
        return this.slice(startrow, endrowexclusive, 0, this.cols);
    }

    public DenseMatrix nonZeroRows() {
        if (this.cols != 1) {
            throw new RuntimeException("cols should be 1 but was " + this.cols);
        } else {
            SparseMatrixLil indices = new SparseMatrixLil(0, 1);
            int resultrow = 0;

            for (int i = 0; i < this.rows; ++i) {
                if (this.values[i] != 0.0D) {
                    indices.append(resultrow, 0, (double) i);
                    ++resultrow;
                }
            }

            indices.rows = resultrow;
            return indices.toDense();
        }
    }

    public DenseMatrix nonZeroCols() {
        if (this.rows != 1) {
            throw new RuntimeException("rows should be 1 but was " + this.rows);
        } else {
            SparseMatrixLil indices = new SparseMatrixLil(0, 1);
            int resultrow = 0;

            for (int i = 0; i < this.cols; ++i) {
                if (this.values[i] != 0.0D) {
                    indices.append(resultrow, 0, (double) i);
                    ++resultrow;
                }
            }

            indices.rows = resultrow;
            return indices.toDense();
        }
    }

    public DenseMatrix slice(int startrow, int endrowexclusive, int startcol, int endcolexclusive) {
        int resultrows = endrowexclusive - startrow;
        int resultcols = endcolexclusive - startcol;
        if (endrowexclusive > this.rows) {
            throw new RuntimeException("endrow must not exceed rows " + endrowexclusive + " vs " + this.rows);
        } else if (endcolexclusive > this.cols) {
            throw new RuntimeException("endcol must not exceed cols " + endcolexclusive + " vs " + this.cols);
        } else if (startrow < 0) {
            throw new RuntimeException("startrow must be at least 0, but was  " + startrow);
        } else if (startcol < 0) {
            throw new RuntimeException("startcol must be at least 0, but was  " + startcol);
        } else {
            DenseMatrix result = new DenseMatrix(resultrows, resultcols);

            for (int c = 0; c < resultcols; ++c) {
                int resultoffset = resultrows * c;
                int sourceoffset = (startcol + c) * this.rows;

                for (int r = 0; r < resultrows; ++r) {
                    result.values[resultoffset + r] = this.values[sourceoffset + startrow + r];
                }
            }

            return result;
        }
    }

    public DenseMatrix select(int[] rowIndices, int[] colIndices) {
        int result;
        for (result = 0; result < rowIndices.length; ++result) {
            if (rowIndices[result] < 0 || rowIndices[result] >= this.rows) {
                throw new RuntimeException("rowIndex must be in [0, " + this.rows + ")");
            }
        }

        for (result = 0; result < colIndices.length; ++result) {
            if (colIndices[result] < 0 || colIndices[result] >= this.cols) {
                throw new RuntimeException("colIndex must be in [0, " + this.cols + ")");
            }
        }

        DenseMatrix var6 = new DenseMatrix(rowIndices.length, colIndices.length);

        for (int i = 0; i < rowIndices.length; ++i) {
            for (int j = 0; j < colIndices.length; ++j) {
                var6.set(i, j, this.get(rowIndices[i], colIndices[j]));
            }
        }

        return var6;
    }

    public DenseMatrix selectRows(int[] rowIndices) {
        for (int result = 0; result < rowIndices.length; ++result) {
            if (rowIndices[result] < 0 || rowIndices[result] >= this.rows) {
                throw new RuntimeException("rowIndex must be in [0, " + this.rows + ")");
            }
        }

        DenseMatrix var5 = new DenseMatrix(rowIndices.length, this.cols);

        for (int i = 0; i < rowIndices.length; ++i) {
            for (int j = 0; j < this.cols; ++j) {
                var5.set(i, j, this.get(rowIndices[i], j));
            }
        }

        return var5;
    }

    public DenseMatrix selectCols(int[] colIndices) {
        for (int result = 0; result < colIndices.length; ++result) {
            if (colIndices[result] < 0 || colIndices[result] >= this.cols) {
                throw new RuntimeException("colIndex must be in [0, " + this.cols + ")");
            }
        }

        DenseMatrix var5 = new DenseMatrix(this.rows, colIndices.length);

        for (int i = 0; i < this.rows; ++i) {
            for (int j = 0; j < colIndices.length; ++j) {
                var5.set(i, j, this.get(i, colIndices[j]));
            }
        }

        return var5;
    }

    public DenseMatrix concatRight(DenseMatrix two) {
        if (this.rows != two.rows) {
            throw new RuntimeException("row mismatch " + this.rows + " vs " + two.rows);
        } else {
            DenseMatrix result = zeros(this.rows, this.cols + two.cols);

            int c;
            int r;
            for (c = 0; c < this.cols; ++c) {
                for (r = 0; r < this.rows; ++r) {
                    result.set(r, c, this.get(r, c));
                }
            }

            for (c = 0; c < two.cols; ++c) {
                for (r = 0; r < this.rows; ++r) {
                    result.set(r, this.cols + c, two.get(r, c));
                }
            }

            return result;
        }
    }

    public DenseMatrix concatDown(DenseMatrix two) {
        if (this.cols != two.cols) {
            throw new RuntimeException("col mismatch " + this.cols + " vs " + two.cols);
        } else {
            DenseMatrix result = zeros(this.rows + two.rows, this.cols);

            int c;
            int r;
            for (c = 0; c < this.cols; ++c) {
                for (r = 0; r < this.rows; ++r) {
                    result.set(r, c, this.get(r, c));
                }
            }

            for (c = 0; c < this.cols; ++c) {
                for (r = 0; r < two.rows; ++r) {
                    result.set(this.rows + r, c, two.get(r, c));
                }
            }

            return result;
        }
    }

    public DenseMatrix diag() {
        if (this.cols != 1) {
            throw new RuntimeException("diag needs a matrix with one column exactly");
        } else {
            int size = this.rows;
            DenseMatrix result = new DenseMatrix(size, size);

            for (int i = 0; i < size; ++i) {
                result.set(i, i, this.get(i, 0));
            }

            return result;
        }
    }

    public DenseMatrix sum() {
        return this.rows > 1 ? this.sumOverRows() : this.sumOverCols();
    }

    public DenseMatrix varOverRows() {
        return Statistics.varOverRows(this);
    }

    public DenseMatrix varOverCols() {
        return Statistics.varOverCols(this);
    }

    public DenseMatrix meanOverRows() {
        return Statistics.meanOverRows(this);
    }

    public DenseMatrix meanOverCols() {
        return Statistics.meanOverCols(this);
    }

    public DenseMatrix sumOverRows() {
        DenseMatrix result = new DenseMatrix(1, this.cols);

        for (int c = 0; c < this.cols; ++c) {
            int offset = c * this.rows;
            double sum = 0.0D;

            for (int r = 0; r < this.rows; ++r) {
                sum += this.values[offset + r];
            }

            result.set(0, c, sum);
        }

        return result;
    }

    public DenseMatrix sumOverCols() {
        DenseMatrix result = new DenseMatrix(this.rows, 1);

        for (int r = 0; r < this.rows; ++r) {
            double sum = 0.0D;

            for (int c = 0; c < this.cols; ++c) {
                sum += this.get(r, c);
            }

            result.set(r, 0, sum);
        }

        return result;
    }

    public DenseMatrix maxOverRows() {
        if (this.cols < 1) {
            throw new RuntimeException("maxoverrows can\'t be called on empty matrix");
        } else {
            DenseMatrix result = new DenseMatrix(1, this.cols);

            for (int c = 0; c < this.cols; ++c) {
                int offset = c * this.rows;
                double max = this.get(0, c);

                for (int r = 0; r < this.rows; ++r) {
                    max = Math.max(max, this.values[offset + r]);
                }

                result.set(0, c, max);
            }

            return result;
        }
    }

    public DenseMatrix maxOverCols() {
        if (this.rows < 1) {
            throw new RuntimeException("maxOverCols can\'t be called on empty matrix");
        } else {
            DenseMatrix result = new DenseMatrix(this.rows, 1);

            for (int r = 0; r < this.rows; ++r) {
                double max = this.get(r, 0);

                for (int c = 0; c < this.cols; ++c) {
                    max = Math.max(max, this.get(r, c));
                }

                result.set(r, 0, max);
            }

            return result;
        }
    }

    public DenseMatrix minOverRows() {
        if (this.cols < 1) {
            throw new RuntimeException("minoverrows can\'t be called on empty matrix");
        } else {
            DenseMatrix result = new DenseMatrix(1, this.cols);

            for (int c = 0; c < this.cols; ++c) {
                int offset = c * this.rows;
                double min = this.get(0, c);

                for (int r = 0; r < this.rows; ++r) {
                    min = Math.min(min, this.values[offset + r]);
                }

                result.set(0, c, min);
            }

            return result;
        }
    }

    public DenseMatrix minOverCols() {
        if (this.rows < 1) {
            throw new RuntimeException("minOverCols can\'t be called on empty matrix");
        } else {
            DenseMatrix result = new DenseMatrix(this.rows, 1);

            for (int r = 0; r < this.rows; ++r) {
                double min = this.get(r, 0);

                for (int c = 0; c < this.cols; ++c) {
                    min = Math.min(min, this.get(r, c));
                }

                result.set(r, 0, min);
            }

            return result;
        }
    }

    public DenseMatrix t() {
        DenseMatrix result = new DenseMatrix(this.cols, this.rows);

        for (int r = 0; r < this.rows; ++r) {
            for (int c = 0; c < this.cols; ++c) {
                result.set(c, r, this.get(r, c));
            }
        }

        return result;
    }

    public final void set(int row, int col, double value) {
        this.values[this.rows * col + row] = value;
    }

    public final void set(int offset, double value) {
        this.values[offset] = value;
    }

    public final double get(int row, int col) {
        return this.values[this.rows * col + row];
    }

    public final double[] getValues() {
        return this.values;
    }

    public DenseMatrix neg() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = -this.values[i];
        }

        return result;
    }

    public DenseMatrix recpr() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = 1.0D / this.values[i];
        }

        return result;
    }

    public DenseMatrix abs() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = Math.abs(this.values[i]);
        }

        return result;
    }

    public DenseMatrix mul(double scalar) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i] * scalar;
        }

        return result;
    }

    public DenseMatrix pow(double power) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = Math.pow(this.values[i], power);
        }

        return result;
    }

    public DenseMatrix sqrt() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = Math.sqrt(this.values[i]);
        }

        return result;
    }

    public DenseMatrix exp() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = Math.exp(this.values[i]);
        }

        return result;
    }

    public DenseMatrix log() {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = Math.log(this.values[i]);
        }

        return result;
    }

    public DenseMatrix div(double scalar) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i] / scalar;
        }

        return result;
    }

    public DenseMatrix add(double scalar) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i] + scalar;
        }

        return result;
    }

    public DenseMatrix sub(double scalar) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int capacity = this.rows * this.cols;

        for (int i = 0; i < capacity; ++i) {
            result.values[i] = this.values[i] - scalar;
        }

        return result;
    }

    public DenseMatrix mul(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int capacity = this.rows * this.cols;

            for (int i = 0; i < capacity; ++i) {
                result.values[i] = this.values[i] * second.values[i];
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch: " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix div(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int capacity = this.rows * this.cols;

            for (int i = 0; i < capacity; ++i) {
                result.values[i] = this.values[i] / second.values[i];
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch: " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix add(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int capacity = this.rows * this.cols;

            for (int i = 0; i < capacity; ++i) {
                result.values[i] = this.values[i] + second.values[i];
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch: " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix sub(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int capacity = this.rows * this.cols;

            for (int i = 0; i < capacity; ++i) {
                result.values[i] = this.values[i] - second.values[i];
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch: " + this.shape() + " vs " + second.shape());
        }
    }

    public boolean equals(Object osecond) {
        if (osecond == null) {
            return false;
        } else {
            DenseMatrix second = null;
            if (osecond instanceof SparseMatrixLil) {
                second = ((SparseMatrixLil) osecond).toDense();
            } else {
                second = (DenseMatrix) osecond;
            }

            if (this.cols == second.cols && this.rows == second.rows) {
                int numElements = this.rows * this.cols;

                for (int i = 0; i < numElements; ++i) {
                    if (Math.abs(this.values[i] - second.values[i]) > 1.0E-6D) {
                        return false;
                    }
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public DenseMatrix eq(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] == s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix ne(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] != s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix le(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] <= s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix ge(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] >= s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix lt(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] < s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix gt(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] > s) {
                result.values[i] = 1.0D;
            }
        }

        return result;
    }

    public DenseMatrix eq(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] == second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix ne(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] != second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix le(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] <= second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix ge(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] >= second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix gt(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] > second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix lt(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] < second.values[i]) {
                    result.values[i] = 1.0D;
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix max(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] < second.values[i]) {
                    result.values[i] = second.values[i];
                } else {
                    result.values[i] = this.values[i];
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix min(DenseMatrix second) {
        if (this.cols == second.cols && this.rows == second.rows) {
            DenseMatrix result = new DenseMatrix(this.rows, this.cols);
            int numElements = this.rows * this.cols;

            for (int i = 0; i < numElements; ++i) {
                if (this.values[i] > second.values[i]) {
                    result.values[i] = second.values[i];
                } else {
                    result.values[i] = this.values[i];
                }
            }

            return result;
        } else {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        }
    }

    public DenseMatrix max(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] < s) {
                result.values[i] = s;
            } else {
                result.values[i] = this.values[i];
            }
        }

        return result;
    }

    public DenseMatrix min(double s) {
        DenseMatrix result = new DenseMatrix(this.rows, this.cols);
        int numElements = this.rows * this.cols;

        for (int i = 0; i < numElements; ++i) {
            if (this.values[i] > s) {
                result.values[i] = s;
            } else {
                result.values[i] = this.values[i];
            }
        }

        return result;
    }

    public DenseMatrix dummy_mmul(DenseMatrix second) {
        if (this.cols != second.rows) {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        } else {
            DenseMatrix result = new DenseMatrix(this.rows, second.cols);
            Jeigen.dense_dummy_op2(this.rows, this.cols, second.cols, this.values, second.values, result.values);
            return result;
        }
    }

    public DenseMatrix mmul(DenseMatrix second) {
        if (this.cols != second.rows) {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        } else {
            DenseMatrix result = new DenseMatrix(this.rows, second.cols);
            Jeigen.dense_multiply(this.rows, this.cols, second.cols, this.values, second.values, result.values);
            return result;
        }
    }

    public DenseMatrix mmul(SparseMatrixLil second) {
        if (this.cols != second.rows) {
            throw new RuntimeException("matrix size mismatch " + this.shape() + " vs " + second.shape());
        } else {
            int twohandle = SparseMatrixLil.allocateSparseMatrix(second);
            DenseMatrix result = new DenseMatrix(this.rows, second.cols);
            Jeigen.dense_sparse_multiply(this.rows, this.cols, second.cols, this.values, twohandle, result.values);
            Jeigen.freeSparseMatrix(twohandle);
            return result;
        }
    }

    public DenseMatrix shape() {
        return new DenseMatrix(new double[][]{{(double) this.rows, (double) this.cols}});
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DenseMatrix, " + this.rows + " * " + this.cols + ":\n");
        stringBuilder.append("\n");
        int rMax;
        int cMax;
        if (this.rows * this.cols <= 1000) {
            for (rMax = 0; rMax < this.rows; ++rMax) {
                for (cMax = 0; cMax < this.cols; ++cMax) {
                    stringBuilder.append(String.format("%8.5f ", new Object[]{Double.valueOf(this.get(rMax, cMax))}));
                }

                stringBuilder.append("\n");
            }
        } else {
            rMax = this.rows > 100 ? 7 : this.rows;
            cMax = this.cols > 100 ? 7 : this.cols;

            for (int i = 0; i < rMax; ++i) {
                int r = this.rows > 100 && i > 3 ? this.rows - i - 1 : i;
                if (this.rows > 100 && i == 3) {
                    stringBuilder.append("...\n");
                }

                for (int j = 0; j < cMax; ++j) {
                    int c = this.cols > 100 && j > 3 ? this.cols - j - 1 : j;
                    if (this.cols > 100 && j == 3) {
                        stringBuilder.append("... ");
                    } else {
                        stringBuilder.append(String.format("%8.5f ", new Object[]{Double.valueOf(this.get(r, c))}));
                    }
                }

                stringBuilder.append("\n");
            }
        }

        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public DenseMatrix ldltSolve(DenseMatrix b) {
        if (this.rows != b.rows) {
            throw new RuntimeException("ldltsolve matrix size mismatch " + this.shape() + " vs " + b.shape());
        } else {
            DenseMatrix result = new DenseMatrix(this.cols, b.cols);
            Jeigen.ldlt_solve(this.rows, this.cols, b.cols, this.values, b.values, result.values);
            return result;
        }
    }

    public DenseMatrix fullPivHouseholderQRSolve(DenseMatrix b) {
        if (this.rows != b.rows) {
            throw new RuntimeException("ldltsolve matrix size mismatch " + this.shape() + " vs " + b.shape());
        } else {
            DenseMatrix result = new DenseMatrix(this.cols, b.cols);
            Jeigen.fullpivhouseholderqr_solve(this.rows, this.cols, b.cols, this.values, b.values, result.values);
            return result;
        }
    }

    public DenseMatrix.EigenResult eig() {
        if (this.cols != this.rows) {
            throw new RuntimeException("eig matrix size error: must be square matrix");
        } else {
            DenseMatrix eigenValuesReal = new DenseMatrix(this.rows, 1);
            DenseMatrix eigenValuesImag = new DenseMatrix(this.rows, 1);
            DenseMatrix eigenVectorsReal = new DenseMatrix(this.cols, this.cols);
            DenseMatrix eigenVectorsImag = new DenseMatrix(this.cols, this.cols);
            Jeigen.jeigen_eig(this.rows, this.values, eigenValuesReal.values, eigenValuesImag.values, eigenVectorsReal.values, eigenVectorsImag.values);
            return new DenseMatrix.EigenResult(new DenseMatrixComplex(eigenValuesReal, eigenValuesImag), new DenseMatrixComplex(eigenVectorsReal, eigenVectorsImag));
        }
    }

    public DenseMatrix.PseudoEigenResult peig() {
        if (this.cols != this.rows) {
            throw new RuntimeException("eig matrix size error: must be square matrix");
        } else {
            DenseMatrix eigenValues = new DenseMatrix(this.rows, this.cols);
            DenseMatrix eigenVectors = new DenseMatrix(this.cols, this.cols);
            Jeigen.jeigen_peig(this.rows, this.values, eigenValues.values, eigenVectors.values);
            return new DenseMatrix.PseudoEigenResult(eigenValues, eigenVectors);
        }
    }

    public DenseMatrix mexp() {
        if (this.cols != this.rows) {
            throw new RuntimeException("exp matrix size error: must be square matrix");
        } else {
            DenseMatrix result = new DenseMatrix(this.cols, this.cols);
            Jeigen.jeigen_exp(this.rows, this.values, result.values);
            return result;
        }
    }

    public DenseMatrix mlog() {
        if (this.cols != this.rows) {
            throw new RuntimeException("log matrix size error: must be square matrix");
        } else {
            DenseMatrix result = new DenseMatrix(this.cols, this.cols);
            Jeigen.jeigen_log(this.rows, this.values, result.values);
            return result;
        }
    }

    public DenseMatrix sortRows(DenseMatrix keyColumns) {
        return DenseSorter.sortRows(this, keyColumns);
    }

    public DenseMatrix sortCols(DenseMatrix keyColumns) {
        return DenseSorter.sortCols(this, keyColumns);
    }

    public DenseMatrix sumOverRows(DenseMatrix keyColumns) {
        return DenseAggregator.sumOverRows(this, keyColumns);
    }

    public DenseMatrix meanOverRows(DenseMatrix keyColumns) {
        return DenseAggregator.meanOverRows(this, keyColumns);
    }

    public DenseMatrix.SvdResult svd() {
        int n = this.rows;
        int p = this.cols;
        int m = Math.min(n, p);
        DenseMatrix U = zeros(n, m);
        DenseMatrix S = zeros(m, 1);
        DenseMatrix V = zeros(p, m);
        Jeigen.svd_dense(this.rows, this.cols, this.values, U.values, S.values, V.values);
        return new DenseMatrix.SvdResult(U, S, V);
    }

    SparseMatrixLil toSparseLil() {
        SparseMatrixLil result = new SparseMatrixLil(this.rows, this.cols);
        int notZero = 0;
        int count = this.rows * this.cols;

        int c;
        for (c = 0; c < count; ++c) {
            if (this.values[c] != 0.0D) {
                ++notZero;
            }
        }

        result.reserve(notZero);

        for (c = 0; c < this.cols; ++c) {
            for (int r = 0; r < this.rows; ++r) {
                double value = this.values[this.rows * c + r];
                if (value != 0.0D) {
                    result.append(r, c, value);
                }
            }
        }

        return result;
    }

    public static class SvdResult {
        public final DenseMatrix U;
        public final DenseMatrix S;
        public final DenseMatrix V;

        public SvdResult(DenseMatrix u, DenseMatrix s, DenseMatrix v) {
            this.U = u;
            this.S = s;
            this.V = v;
        }
    }

    public static class PseudoEigenResult {
        public DenseMatrix values;
        public DenseMatrix vectors;

        public PseudoEigenResult(DenseMatrix eigenValues, DenseMatrix eigenVectors) {
            this.values = eigenValues;
            this.vectors = eigenVectors;
        }
    }

    public static class EigenResult {
        public DenseMatrixComplex values;
        public DenseMatrixComplex vectors;

        public EigenResult(DenseMatrixComplex values, DenseMatrixComplex vectors) {
            this.values = values;
            this.vectors = vectors;
        }
    }
}
