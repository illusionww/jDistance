package com.thesis.metric.builder;

import com.thesis.utils.BigDecimalMatrix;
import org.nevec.rjm.BigDecimalMath;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public class BigDecimalHelper {
    public static BigDecimalMatrix log(BigDecimalMatrix A) {
        return elementWise(A, BigDecimalMath::log);
    }

    public static BigDecimalMatrix exp(BigDecimalMatrix A) {
        return elementWise(A, BigDecimalMath::exp);
    }

    private static BigDecimalMatrix elementWise(BigDecimalMatrix A, UnaryOperator<BigDecimal> operator) {
        BigDecimal[][] values = toArray2(A);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = operator.apply(values[i][j]);
            }
        }
        return new BigDecimalMatrix(values);
    }

    public static BigDecimalMatrix diagToVector(BigDecimalMatrix A) {
        BigDecimalMatrix diag = new BigDecimalMatrix(A.rows, 1);
        BigDecimal[] values = A.getValues();
        for (int i = 0; i < A.rows; i++) {
            diag.set(i, values[i * (A.cols + 1)]);
        }
        return diag;
    }

    public static BigDecimalMatrix normalization(BigDecimalMatrix dm) {
        BigDecimal avg = dm.sum().sum().s().divide(new BigDecimal(dm.cols * (dm.cols - 1)));
        return dm.div(avg);
    }

    public static BigDecimal[][] toArray2(BigDecimalMatrix dm) {
        BigDecimal[] values = dm.getValues();
        BigDecimal[][] newValues = new BigDecimal[dm.cols][dm.rows];
        for (int i = 0; i < dm.cols; i++) {
            System.arraycopy(values, i * dm.rows, newValues[i], 0, dm.rows);
        }
        return newValues;
    }
}
