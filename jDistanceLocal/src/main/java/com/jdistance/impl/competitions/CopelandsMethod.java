package com.jdistance.impl.competitions;

import com.jdistance.distance.AbstractMeasureWrapper;
import com.jdistance.graph.Graph;
import com.jdistance.graph.GraphBundle;
import com.jdistance.graph.generator.GeneratorPropertiesPOJO;
import com.jdistance.graph.generator.GnPInPOutGraphGenerator;
import com.jdistance.impl.Main;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.DefaultHashMap;
import com.jdistance.impl.workflow.TaskPool;
import com.jdistance.impl.workflow.TaskPoolResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class CopelandsMethod {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private List<GeneratorPropertiesPOJO> graphParameters;
    protected List<? extends AbstractMeasureWrapper> measures;
    protected int pointsCount;
    private double percentile;

    private Map<String, Map<String, Double>> bestResults = new TreeMap<>();
    private Map<String, Map<String, Double>> percentileResults = new TreeMap<>();

    protected CopelandsMethod(List<GeneratorPropertiesPOJO> graphParameters, List<? extends AbstractMeasureWrapper> measures, int pointsCount, double percentile) {
        this.graphParameters = graphParameters;
        this.measures = measures;
        this.pointsCount = pointsCount;
        this.percentile = percentile;
    }

    public void execute() {
        for (GeneratorPropertiesPOJO properties : graphParameters) {
            String currentName = "n=" + properties.getNodesCount() + " k=" + properties.getClustersCount() + " pOut=" + properties.getP_out();

            Map<String, Double> currentCompetitionBestResults = new DefaultHashMap<>(0.0);
            Map<String, Double> currentCompetitionPercentileResults = new DefaultHashMap<>(0.0);
            GraphBundle graphs = new GnPInPOutGraphGenerator().generate(properties);
            for (Graph graph : graphs.getGraphs()) {
                TaskPool pool = generateTaskPool(new GraphBundle(Collections.singletonList(graph), graphs.getProperties()));
                TaskPoolResult result = pool.execute();
                updateBestResults(result, currentCompetitionBestResults);
                updatePercentileResults(result, currentCompetitionPercentileResults);
            }
            bestResults.put(currentName, currentCompetitionBestResults);
            percentileResults.put(currentName, currentCompetitionPercentileResults);
        }
    }

    private void updateBestResults(TaskPoolResult result, Map<String, Double> competitionResults) {
        for (String task1 : result.getTaskNames()) {
            for (String task2 : result.getTaskNames()) {
                Double bestValue1 = result.getBestParam(task1).getValue();
                Double bestValue2 = result.getBestParam(task2).getValue();
                int verdict = (bestValue1 > bestValue2) ? 1 : (bestValue1 < bestValue2) ? -1 : 0;
                competitionResults.put(task1, competitionResults.get(task1) + 0.5 * verdict);
                competitionResults.put(task2, competitionResults.get(task2) - 0.5 * verdict);
            }
        }
    }

    private void updatePercentileResults(TaskPoolResult result, Map<String, Double> competitionResults) {
        for (String task1 : result.getTaskNames()) {
            for (String task2 : result.getTaskNames()) {
                Double bestValue1 = result.getQuantile(task1, percentile);
                Double bestValue2 = result.getQuantile(task2, percentile);
                int verdict = (bestValue1 > bestValue2) ? 1 : (bestValue1 < bestValue2) ? -1 : 0;
                competitionResults.put(task1, competitionResults.get(task1) + 0.5 * verdict);
                competitionResults.put(task2, competitionResults.get(task2) - 0.5 * verdict);
            }
        }
    }

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
