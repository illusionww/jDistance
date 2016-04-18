package com.jdistance.learning;

import jeigen.DenseMatrix;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Ward implements Estimator {
    private int nClusters;

    public Ward(int nClusters) {
        this.nClusters = nClusters;
    }

    public String getName() {
        return "Ward";
    }

    public HashMap<Integer, Integer> predict(DenseMatrix K) {
        List<Cluster> clusters = new LinkedList<>(IntStream.range(0, K.cols)
                .mapToObj(i -> new Cluster(Collections.singletonList(i), K.cols))
                .collect(Collectors.toList()));
        Map<Pair<Cluster, Cluster>, Double> ΔJ = new HashMap<>();

        for (int i = 0; i < K.cols - nClusters; i++) {
            iteration(K, clusters, ΔJ);
        }

        HashMap<Integer, Integer> result = new HashMap<>();
        for (int clusterId = 0; clusterId < clusters.size(); clusterId++) {
            for (Integer nodeId : clusters.get(clusterId).nodes) {
                result.put(nodeId, clusterId);
            }
        }
        return result;
    }

    private void iteration(DenseMatrix K, List<Cluster> clusters, Map<Pair<Cluster, Cluster>, Double> ΔJ) {
        Cluster minCk = null;
        Cluster minCl = null;
        double minΔJ = Double.MAX_VALUE;
        for (int k = 0; k < clusters.size(); k++) {
            Cluster Ck = clusters.get(k);
            for (int l = k + 1; l < clusters.size(); l++) {
                Cluster Cl = clusters.get(l);
                Double currentΔJ = ΔJ.get(new ImmutablePair<>(Ck, Cl));
                if (currentΔJ == null) {
                    currentΔJ = calcΔJ(K, ΔJ, Ck, Cl);
                }
                if (currentΔJ < minΔJ) {
                    minCk = Ck;
                    minCl = Cl;
                    minΔJ = currentΔJ;
                }
            }
        }
        merge(K, clusters, minCk, minCl);
    }

    private void merge(DenseMatrix K, List<Cluster> clusters, Cluster Ck, Cluster Cl) {
        List<Integer> union = new ArrayList<>(Ck.nodes);
        union.addAll(Cl.nodes);
        clusters.remove(Cl);
        clusters.remove(Ck);
        clusters.add(new Cluster(union, K.rows));
    }

    // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
    private double calcΔJ(DenseMatrix K, Map<Pair<Cluster, Cluster>, Double> ΔJ, Cluster Ck, Cluster Cl) {
        double norm = Ck.n * Cl.n / (double) (Ck.n + Cl.n);
        DenseMatrix hkhl = (Ck.h).sub(Cl.h);
        double currentΔJ = hkhl.t().mmul(K).mmul(hkhl).mul(norm).s();
        ΔJ.put(new ImmutablePair<>(Ck, Cl), currentΔJ);
        return currentΔJ;
    }

    private class Cluster {
        List<Integer> nodes;
        DenseMatrix h;
        int n;

        Cluster(List<Integer> nodes, int length) {
            this.nodes = nodes;
            n = nodes.size();
            h = DenseMatrix.zeros(length, 1);
            refreshH();
        }

        void refreshH() {
            h = DenseMatrix.zeros(h.rows, 1);
            for (Integer node : nodes) {
                h.set(node, 0, 1 / (double) n);
            }
        }
    }
}
