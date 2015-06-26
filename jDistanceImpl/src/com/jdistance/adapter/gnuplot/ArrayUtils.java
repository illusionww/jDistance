package com.jdistance.adapter.gnuplot;

import com.panayotis.gnuplot.dataset.Point;

import java.util.*;

public class ArrayUtils {
    public static List<Point<Double>> mapToPoints(Map<Double, Double> results) {
        List<Point<Double>> list = new ArrayList<>();
        SortedSet<Double> keys = new TreeSet<>(results.keySet());
        for (Double key : keys) {
            Double value = results.get(key);
            list.add(new Point<>(key, value));
        }
        return list;
    }
}
