package com.jdistance.impl;

import com.graphgenerator.utils.Input;
import com.graphgenerator.utils.ParseInput;
import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.checker.ClassifierChecker;
import com.jdistance.impl.workflow.checker.ClustererChecker;
import com.jdistance.impl.workflow.task.DefaultTask;
import com.jdistance.impl.workflow.task.competition.CompetitionDTO;
import com.jdistance.impl.workflow.task.competition.MetricCompetitionTask;
import com.jdistance.metric.Distance;
import com.jdistance.metric.DistanceClass;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        callMetricCompetitionTask("./dataForGenerator/k5n200pin0.4pout0.05.txt");
    }

    private static void initContext() {
        Context context = Context.getInstance();
        context.GNUPLOT_PATH = "c:\\cygwin64\\bin\\gnuplot.exe";
        context.IMG_FOLDER = "pictures";
        new File("./pictures").mkdir();
        context.COMPETITION_FOLDER = "tournament";
        new File("./tournament").mkdir();
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }

    private static void drawGraphsScenario(String pathToParameters) throws ParserConfigurationException, SAXException, IOException {
        List<Distance> distances = Arrays.asList(
                DistanceClass.COMM_D.getInstance(),
                DistanceClass.LOG_COMM_D.getInstance(),
                DistanceClass.SP_CT.getInstance(),
                DistanceClass.FREE_ENERGY.getInstance(),
                DistanceClass.WALK.getInstance(),
                DistanceClass.LOG_FOREST.getInstance(),
                DistanceClass.FOREST.getInstance(),
                DistanceClass.PLAIN_WALK.getInstance()
        );
        Input input = ParseInput.parse(pathToParameters);

        Arrays.asList(5).forEach(graphCount -> {
                    GraphBundle graphs = new GraphBundle(input, graphCount);
                    ScenarioHelper.defaultTasks(new ClustererChecker(graphs, 5), distances, 250).execute().draw();
        });
    }

    private static void callMetricCompetitionTask(String pathToParameters) {
        Input input = ParseInput.parse(pathToParameters);
        int k = input.getSizeOfVertices().size();
        int graphCountForLearning = 5;
        int graphCountForCompetitions = 5;
        int pointsCount = 5;

        List<CompetitionDTO> competitionDTOs = DistanceClass.getDefaultDistances().stream()
                .map(distanceClass -> new CompetitionDTO(distanceClass.getInstance(), k)).collect(Collectors.toList());

        GraphBundle graphsForLearning = new GraphBundle(input, graphCountForLearning);
        GraphBundle graphsForCompetitions = new GraphBundle(input, graphCountForCompetitions);

        MetricCompetitionTask task = new MetricCompetitionTask(competitionDTOs, graphsForLearning, graphsForCompetitions, pointsCount, "test");
        task.execute().write();
    }
}

