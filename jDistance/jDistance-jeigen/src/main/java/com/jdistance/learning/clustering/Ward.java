package com.jdistance.learning.clustering;

import com.jdistance.structures.LinkedList;
import com.jdistance.structures.LinkedList.Node;
import com.jdistance.learning.Estimator;
import jeigen.DenseMatrix;

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

    public Map<Integer, Integer> predict(DenseMatrix K) {
        LinkedList<Cluster> clusters = new LinkedList<>(IntStream.range(0, K.cols)
                .mapToObj(i -> new Cluster(Collections.singletonList(i), K.cols))
                .collect(Collectors.toList()));
        for (int i = 0; i < K.cols - nClusters; i++) {
            iteration(K, clusters);
        }

        HashMap<Integer, Integer> result = new HashMap<>();
        Node<Cluster> clusterNode = clusters.first;
        for (int clusterId = 0; clusterId < clusters.size(); clusterId++) {
            for (Integer nodeId : clusterNode.item.nodes) {
                result.put(nodeId, clusterId);
            }
            clusterNode = clusterNode.next;
        }
        return result;
    }

    private void iteration(DenseMatrix K, LinkedList<Cluster> clusters) {
        Node<Cluster> Ck = clusters.first, Cl = clusters.first, minCk = null, minCl = null;
        double currentΔJ, minΔJ = Double.MAX_VALUE;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                Cl = Cl.next;
                currentΔJ = Ck.item.getΔJ(K, Cl.item);
                if (currentΔJ < minΔJ) {
                    minCk = Ck;
                    minCl = Cl;
                    minΔJ = currentΔJ;
                }
            }
            Ck = Ck.next;
            Cl = Ck;
        }
        merge(K, clusters, minCk, minCl);
    }

    private void merge(DenseMatrix K, LinkedList<Cluster> clusters, Node<Cluster> Ck, Node<Cluster> Cl) {
        List<Integer> union = new ArrayList<>(Ck.item.nodes);
        union.addAll(Cl.item.nodes);
        clusters.unlink(Cl);
        clusters.unlink(Ck);
        clusters.linkLast(new Cluster(union, K.rows));
    }

    private class Cluster {
        List<Integer> nodes;
        double n;
        DenseMatrix h;

        private Map<Cluster, Double> ΔJ;

        Cluster(List<Integer> nodes, int allNodesCount) {
            this.nodes = nodes;
            n = (double) nodes.size();

            h = DenseMatrix.zeros(allNodesCount, 1);
            double inverseN = 1.0 / n;
            for (Integer node : nodes) {
                h.set(node, 0, inverseN);
            }

            ΔJ = new HashMap<>((int) n / 2);
        }

        double getΔJ(DenseMatrix K, Cluster Cl) {
            Double currentΔJ = ΔJ.get(Cl);
            if (currentΔJ == null) {
                return calcΔJ(K, Cl);
            } else {
                return currentΔJ;
            }
        }

        // ΔJ = (n_k * n_l)/(n_k + n_l) * (h_k - h_l)^T * K * (h_k - h_l)
        private double calcΔJ(DenseMatrix K, Cluster Cl) {
            DenseMatrix hkhl = (this.h).sub(Cl.h);
            DenseMatrix hkhlT = new DenseMatrix(1, h.rows, hkhl.getValues());
            double currentΔJ = this.n * Cl.n * hkhlT.mmul(K).mmul(hkhl).s() / (this.n + Cl.n);
            ΔJ.put(Cl, currentΔJ);
            return currentΔJ;
        }
    }
}
