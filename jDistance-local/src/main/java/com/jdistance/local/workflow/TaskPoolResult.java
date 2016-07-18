package com.jdistance.local.workflow;

import com.jdistance.local.adapter.GNUPlotAdapter;
import com.jdistance.workflow.AbstractTaskPoolResult;
import com.panayotis.gnuplot.style.Smooth;
import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskPoolResult extends AbstractTaskPoolResult {
    private static final Logger log = LoggerFactory.getLogger(TaskPoolResult.class);

    public TaskPoolResult(String name, List<String> taskNames, Map<String, Map<Double, Double>> data) {
        super(name, taskNames, data);
    }

    public Map.Entry<Double, Double> getBestParam(String taskName) {
        Map<Double, Double> scores = data.get(taskName);
        Optional<Map.Entry<Double, Double>> maxOptional = scores.entrySet().stream()
                .filter(entry -> !entry.getValue().isNaN())
                .max(Map.Entry.comparingByValue(Double::compareTo));
        return maxOptional.isPresent() ? maxOptional.get() : null;
    }

    public Double getQuantile(String taskName, double quantile) {
        return new Percentile().evaluate(data.get(taskName).values().stream()
                .filter(i -> !i.isNaN())
                .mapToDouble(i -> i).toArray(), quantile);
    }

    public TaskPoolResult writeData() {
        writeData(Context.getInstance().buildOutputDataFullName(name, "csv"));
        return this;
    }

    public TaskPoolResult drawUnique(String xrange, String xticks, String yrange, String yticks) {
        draw(name, xrange, xticks, yrange, yticks, Smooth.UNIQUE);
        return this;
    }

    public TaskPoolResult drawUnique(String yrange, String yticks) {
        draw(name, "[0:1]", "0.2", yrange, yticks, Smooth.UNIQUE);
        return this;
    }

    public TaskPoolResult drawUniqueAndBezier(String yrange, String yticks) {
        draw(name + ".UNIQUE", "[0:1]", "0.2", yrange, yticks, Smooth.UNIQUE);
        draw(name + ".BEZIER", "[0:1]", "0.2", yrange, yticks, Smooth.BEZIER);
        return this;
    }

    public TaskPoolResult drawUniqueAndBezier(String xrange, String xticks, String yrange, String yticks) {
        draw(name + ".UNIQUE", xrange, xticks, yrange, yticks, Smooth.UNIQUE);
        draw(name + ".BEZIER", xrange, xticks, yrange, yticks, Smooth.BEZIER);
        return this;
    }

    public TaskPoolResult draw(String imgTitle, String yrange, String yticks, Smooth smooth) {
        draw(imgTitle, "[0:1]", "0.2", yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult draw(String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        drawByName(taskNames, imgTitle, xrange, xticks, yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult drawByName(List<String> taskNames, String imgTitle, String yrange, String yticks, Smooth smooth) {
        drawByName(taskNames, imgTitle, "[0:1]", "0.2", yrange, yticks, smooth);
        return this;
    }

    public TaskPoolResult drawByName(List<String> taskNames, String imgTitle, String xrange, String xticks, String yrange, String yticks, Smooth smooth) {
        log.info("Draw " + imgTitle);
        try {
            GNUPlotAdapter ga = new GNUPlotAdapter();
            ga.draw(taskNames, data, imgTitle, xrange, xticks, yrange, yticks, smooth);
        } catch (RuntimeException e) {
            log.error("RuntimeException while write picture", e);
        }
        return this;
    }
}
