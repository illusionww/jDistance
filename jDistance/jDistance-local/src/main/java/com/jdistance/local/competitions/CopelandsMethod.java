package com.jdistance.local.competitions;

import com.jdistance.local.Main;
import com.jdistance.local.workflow.Context;
import com.jdistance.local.workflow.TaskPool;
import com.jdistance.learning.measure.AbstractMeasureWrapper;
import com.jdistance.graph.GraphBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class CopelandsMethod {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    protected List<? extends AbstractMeasureWrapper> measures;
    protected int pointsCount;
    private List<GraphBundle> graphsList;
    private double percentile;

    private Map<String, Map<String, Double>> bestResults = new TreeMap<>();
    private Map<String, Map<String, Double>> percentileResults = new TreeMap<>();

    protected CopelandsMethod(List<GraphBundle> graphsList, List<? extends AbstractMeasureWrapper> measures, int pointsCount, double percentile) {
        this.graphsList = graphsList;
        this.measures = measures;
        this.pointsCount = pointsCount;
        this.percentile = percentile;
    }

//    public void execute() {
//        for (GraphBundle graphs : graphsList) {
//            String currentName = "n=" + graphs.getProperties().getNodesCount() + " k=" + graphs.getProperties().getClustersCount() + " pOut=" + graphs.getProperties().getP_out();
//
//            Map<String, Double> currentCompetitionBestResults = new DefaultHashMap<>(0.0);
//            Map<String, Double> currentCompetitionPercentileResults = new DefaultHashMap<>(0.0);
//            for (Graph graph : graphs.getGraphs()) {
//                TaskPool pool = generateTaskPool(new GraphBundle(Collections.singletonList(graph), graphs.getProperties()));
//                GridSearchResult result = pool.execute();
//                updateBestResults(result, currentCompetitionBestResults);
//                updatePercentileResults(result, currentCompetitionPercentileResults);
//            }
//            bestResults.put(currentName, currentCompetitionBestResults);
//            percentileResults.put(currentName, currentCompetitionPercentileResults);
//        }
//    }

//    private void updateBestResults(GridSearchResult result, Map<String, Double> competitionResults) {
//        for (String task1 : result.getTaskNames()) {
//            for (String task2 : result.getTaskNames()) {
//                Double bestValue1 = result.getBestParam(task1).getValue();
//                Double bestValue2 = result.getBestParam(task2).getValue();
//                int verdict = (bestValue1 > bestValue2) ? 1 : (bestValue1 < bestValue2) ? -1 : 0;
//                competitionResults.put(task1, competitionResults.get(task1) + 0.5 * verdict);
//                competitionResults.put(task2, competitionResults.get(task2) - 0.5 * verdict);
//            }
//        }
//    }
//
//    private void updatePercentileResults(GridSearchResult result, Map<String, Double> competitionResults) {
//        for (String task1 : result.getTaskNames()) {
//            for (String task2 : result.getTaskNames()) {
//                Double bestValue1 = result.getQuantile(task1, percentile);
//                Double bestValue2 = result.getQuantile(task2, percentile);
//                int verdict = (bestValue1 > bestValue2) ? 1 : (bestValue1 < bestValue2) ? -1 : 0;
//                competitionResults.put(task1, competitionResults.get(task1) + 0.5 * verdict);
//                competitionResults.put(task2, competitionResults.get(task2) - 0.5 * verdict);
//            }
//        }
//    }

    protected abstract TaskPool generateTaskPool(GraphBundle graphs);

    public void write(String name) {
        write(name + "_best", bestResults);
        write(name + "_percentile", percentileResults);
    }

    private void write(String fileName, Map<String, Map<String, Double>> results) {
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(Context.getInstance().buildOutputDataFullName(fileName, "csv")))) {
            outputWriter.write(fileName + "\t");
            for (String name : results.keySet()) {
                outputWriter.write(name + "\t");
            }
            outputWriter.newLine();
            Set<String> metrics = results.values().iterator().next().keySet();
            for (String metric : metrics) {
                outputWriter.write(metric + "\t");
                for (Map<String, Double> resultByMetric : results.values()) {
                    outputWriter.write(resultByMetric.get(metric) + "\t");
                }
                outputWriter.newLine();
            }
        } catch (IOException e) {
            log.error("IOException while write results", e);
        }
    }
}
