package com.jdistance.learning;

import com.jdistance.learning.helper.Cluster;
import com.jdistance.learning.helper.LinkedList;
import jeigen.DenseMatrix;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum Estimator {
    NULL_ESTIMATOR("Null"),
    WARD("Ward") {
        @Override
        public Map<Integer, Integer> predict(DenseMatrix K, Integer clustersCount) {
            com.jdistance.learning.helper.LinkedList<Cluster> clusters = new com.jdistance.learning.helper.LinkedList<>(IntStream.range(0, K.cols)
                    .mapToObj(i -> new Cluster(Collections.singletonList(i), K.cols))
                    .collect(Collectors.toList()));
            for (int i = 0; i < K.cols - clustersCount; i++) {
                iteration(K, clusters);
            }

            HashMap<Integer, Integer> result = new HashMap<>();
            LinkedList.Node<Cluster> clusterNode = clusters.first;
            for (int clusterId = 0; clusterId < clusters.size(); clusterId++) {
                for (Integer nodeId : clusterNode.item.nodes) {
                    result.put(nodeId, clusterId);
                }
                clusterNode = clusterNode.next;
            }
            return result;
        }

        private void iteration(DenseMatrix K, LinkedList<Cluster> clusters) {
            LinkedList.Node<Cluster> Ck = clusters.first, Cl = clusters.first, minCk = null, minCl = null;
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

        private void merge(DenseMatrix K, LinkedList<Cluster> clusters, LinkedList.Node<Cluster> Ck, LinkedList.Node<Cluster> Cl) {
            List<Integer> union = new ArrayList<>(Ck.item.nodes);
            union.addAll(Cl.item.nodes);
            clusters.unlink(Cl);
            clusters.unlink(Ck);
            clusters.linkLast(new Cluster(union, K.rows));
        }
    };

    private String name;

    Estimator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Integer> predict(DenseMatrix D, Integer clustersCount) {
        return null;
    }

}
