package com.jdistance.clusterer;

import com.jdistance.utils.MatrixAdapter;
import jeigen.DenseMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Clusterer {
    private double[][] matrixWithWeights;

    public Clusterer(DenseMatrix matrixWithWeights) {
        this.matrixWithWeights = MatrixAdapter.toArray2(matrixWithWeights);
    }

    //k количество кластеров
    public HashMap<Integer, Integer> predictClusterer(Integer k) {
        double minWeight = Double.MAX_VALUE;
        int to = 0;
        int from = 0;
        HashMap<Integer, Integer> predictCluster = new HashMap<>();
        double[][] sparseMatrix = new double[matrixWithWeights.length][matrixWithWeights.length];

        for (int i = 0; i < matrixWithWeights.length; ++i) {
            for (int j = i + 1; j < matrixWithWeights.length; ++j) {
                if (minWeight > matrixWithWeights[i][j]) {
                    minWeight = matrixWithWeights[i][j];
                    from = i;
                    to = j;
                }
            }
        }
        sparseMatrix[to][from] = minWeight;
        sparseMatrix[from][to] = minWeight;
        matrixWithWeights[to][from] = Double.MAX_VALUE;
        matrixWithWeights[from][to] = Double.MAX_VALUE;
        HashSet<Integer> set = new HashSet<>();
        set.add(from);
        set.add(to);

        while (set.size() < matrixWithWeights.length) {
            minWeight = Double.MAX_VALUE;

            Iterator<Integer> iterator = set.iterator();
            int i;
            while (iterator.hasNext()) {
                i = iterator.next();
                for (int j = 0; j < matrixWithWeights.length; ++j) {
                    if (i != j && minWeight > matrixWithWeights[i][j] && !set.contains(j)) {
                        minWeight = matrixWithWeights[i][j];
                        from = i;
                        to = j;
                    }
                }
            }
            set.add(to);
            sparseMatrix[to][from] = minWeight;
            sparseMatrix[from][to] = minWeight;
            matrixWithWeights[to][from] = Double.MAX_VALUE;
            matrixWithWeights[from][to] = Double.MAX_VALUE;
        }

        double maxWeight;
        for (int i = 0; i < k - 1; ++i) {
            maxWeight = Double.MIN_VALUE;
            for (int j = 0; j < sparseMatrix.length; ++j)
                for (int s = j + 1; s < sparseMatrix.length; ++s) {
                    if (maxWeight < sparseMatrix[j][s]) {
                        maxWeight = sparseMatrix[j][s];
                        from = j;
                        to = s;
                    }
                }
            sparseMatrix[from][to] = 0d;
            sparseMatrix[to][from] = 0d;
        }

        int countCluster = 0;
        Stack<Integer> stack = new Stack<>();
        set.clear();
        while (set.size() < sparseMatrix.length) {
            for (int j = 0; j < sparseMatrix.length; j++) {
                if (stack.empty() && !set.contains(j)) {
                    countCluster++;
                    stack.add(j);
                    set.add(j);
                    predictCluster.put(j, countCluster);
                }

                while (!stack.empty()) {
                    Integer index = stack.pop();
                    for (int i = 0; i < sparseMatrix.length; ++i) {
                        if (index != i && !new Double(0).equals(sparseMatrix[index][i]) && !set.contains(i)) {
                            stack.push(i);
                            predictCluster.put(i, countCluster);
                            set.add(i);
                        }
                    }
                }
            }
        }
        return predictCluster;
    }

    public double[][] getMatrixWithWeights() {
        return this.matrixWithWeights;
    }

    public void setMatrixWithWeights(double[][] matrixWithWeights) {
        this.matrixWithWeights = matrixWithWeights;
    }
}