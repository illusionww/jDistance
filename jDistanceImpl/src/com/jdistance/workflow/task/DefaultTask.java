package com.jdistance.workflow.task;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.cache.CacheItem;
import com.jdistance.cache.CacheManager;
import com.jdistance.graph.Graph;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.checker.Checker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultTask extends Task {
    private static final Logger log = LoggerFactory.getLogger(DefaultTask.class);

    private Distance distance;
    private Checker checker;
    private Integer pointsCount;
    private Map<Double, Double> result;

    public DefaultTask(Checker checker, Distance distance, Integer pointsCount) {
        this.distance = distance;
        this.checker = checker;
        this.pointsCount = pointsCount;
    }

    @Override
    public String getName() {
        return DistanceClass.getDistanceName(distance) + " " + checker.getName() + ", pointsCount=" + pointsCount + " " + distance.getScale();
    }

    @Override
    public Distance getDistance() {
        return distance;
    }

    @Override
    public Task execute() {
        if (Context.getInstance().USE_CACHE) {
            executeUseCache();
        } else {
            executeInternal(false);
        }
        return this;
    }

    private void executeUseCache() {
        GraphBundle bundle = checker.getGraphBundle();
        List<Graph> graphs = bundle.getGraphs();
        int count = bundle.getCount();

        List<CacheItem> items = CacheManager.getInstance().pop(this);
        int cached = items.stream().mapToInt(CacheItem::getCount).sum();
        int left = count - cached;
        if (cached > 0 && left > 0) {
            bundle.setGraphs(bundle.getGraphs().subList(0, left));
        }
        if (left > 0) {
            executeInternal(true);
            items.add(new CacheItem(this));
        }
        if (cached > 0) {
            Map<Double, List<Map<String, Object>>> rawData = new HashMap<>();
            for(CacheItem item : items) {
                Map<Double, Double> itemData = item.getData();
                for(Double x : itemData.keySet()) {
                    List<Map<String, Object>> list = rawData.get(x);
                    if (list == null) {
                        list = new ArrayList<>();
                        rawData.put(x, list);
                    }

                    Map<String, Object> element = new HashMap<>();
                    element.put("value", itemData.get(x));
                    element.put("count", item.getCount());
                    list.add(element);
                }
            }
            Map<Double, Double> data = new HashMap<>();
            for (Double x : rawData.keySet()) {
                Double y = rawData.get(x).stream().mapToDouble(a -> (Double) a.get("value") * (double)(Integer) a.get("count") / (double) count).sum();
                data.put(x, y);
            }
            result = data;
        }
        bundle.setGraphs(graphs);
    }

    private void executeInternal(boolean cache) {
        Map<Double, Double> distanceResult = checker.seriesOfTests(distance, 0.00001, 0.99999, pointsCount);
        result = removeNaN(distanceResult);

        if (cache) {
            CacheItem item = new CacheItem(this);
            CacheManager.getInstance().push(item);
        }
    }

    @Override
    public Map<Double, Double> getResults() {
        return result;
    }

    private Map<Double, Double> removeNaN(Map<Double, Double> distanceResult) {
        return distanceResult.entrySet().stream().filter(entry -> !Double.isNaN(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
