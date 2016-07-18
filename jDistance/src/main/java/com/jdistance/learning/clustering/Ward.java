package com.jdistance.learning.clustering;

import com.jdistance.learning.Estimator;
import com.jdistance.learning.gridsearch.LinkedList;
import org.jblas.DoubleMatrix;

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

    public Map<Integer, Integer> predict(DoubleMatrix K) {
        LinkedList<Cluster> clusters = new LinkedList<>(IntStream.range(0, K.columns)
                .mapToObj(i -> new Cluster(Collections.singletonList(i), K.columns))
                .collect(Collectors.toList()));
        for (int i = 0; i < K.columns - nClusters; i++) {
            iteration(K, clusters);
        }

        HashMap<Integer, Integer> result = new HashMap<>(256);
        LinkedList.Node<Cluster> clusterNode = clusters.first;
        for (int clusterId = 0; clusterId < clusters.size; clusterId++) {
            for (Integer nodeId : clusterNode.item.nodes) {
                result.put(nodeId, clusterId);
            }
            clusterNode = clusterNode.next;
        }
        return result;
    }

    private void iteration(DoubleMatrix K, LinkedList<Cluster> clusters) {
        LinkedList.Node<Cluster> Ck = clusters.first, Cl = clusters.first, minCk = null, minCl = null;
        double currentΔJ, minΔJ = Double.MAX_VALUE;
        for (int k = 0; k < clusters.size; k++) {
            for (int l = k + 1; l < clusters.size; l++) {
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

    private void merge(DoubleMatrix K, LinkedList<Cluster> clusters, LinkedList.Node<Cluster> Ck, LinkedList.Node<Cluster> Cl) {
        List<Integer> union = new ArrayList<>(Ck.item.nodes);
        union.addAll(Cl.item.nodes);
        clusters.unlink(Cl);
        clusters.unlink(Ck);
        clusters.linkLast(new Cluster(union, K.rows));
    }
}
