package com.jdistance.learning.clusterer;

import java.util.HashMap;

public interface Clusterer {
    /**
     * @param k количество кластеров
     * @return \<node index, cluster number\>
     */
    HashMap<Integer, Integer> predict(Integer k);
}
