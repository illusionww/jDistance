package com.thesis.utils;

import com.panayotis.gnuplot.dataset.Point;

import java.util.*;

public class ArrayUtils {
    public static <T extends Cloneable<T>> ArrayList<T> deepCopy(ArrayList<T> original) throws CloneNotSupportedException {
        if (original == null) {
            return null;
        }

        final ArrayList<T> result = new ArrayList<>();
        for (int i = 0; i < original.size(); i++) {
            result.add(i, original.get(i).clone());
        }
        return result;
    }

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
