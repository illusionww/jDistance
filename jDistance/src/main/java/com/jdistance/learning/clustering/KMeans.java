package com.jdistance.learning.clustering;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import com.jdistance.distance.Shortcuts;
import com.jdistance.learning.Estimator;
import com.trickl.cluster.KernelKMeans;
import jeigen.DenseMatrix;

import java.util.*;

public class KMeans implements Estimator {
    private int nClusters;

    public KMeans(int nClusters) {
        this.nClusters = nClusters;
    }

    public String getName() {
        return "k-means";
    }

    @Override
    public Map<Integer, Integer> predict(DenseMatrix K) {
        KernelKMeans kernelKMeans = new KernelKMeans();
        kernelKMeans.cluster(new DenseDoubleMatrix2D(Shortcuts.toArray2(K)), nClusters);
        DoubleMatrix2D partition = kernelKMeans.getPartition();

        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < K.rows; i++) {
            for (int j = 0; j < nClusters; j++) {
                if (partition.getQuick(i, j) == 1.0) {
                    result.put(i, j);
                }
            }
        }
        return result;
    }
}