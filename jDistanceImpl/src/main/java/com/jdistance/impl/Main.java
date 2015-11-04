package com.jdistance.impl;

import com.jdistance.impl.adapter.generator.GraphBundle;
import com.jdistance.impl.workflow.Context;
import com.jdistance.impl.workflow.task.competition.CompetitionDTO;
import com.jdistance.impl.workflow.task.competition.MetricCompetitionTask;
import com.jdistance.metric.DistanceClass;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        initContext();
        callMetricCompetitionTask();
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

    private static void callMetricCompetitionTask() {
        int N = 200;
        double pIn = 0.4;
        double pOut = 0.1;
        int k = 5;
        int graphCountForLearning = 5;
        int graphCountForCompetitions = 5;
        int pointsCount = 5;

        List<CompetitionDTO> competitionDTOs = DistanceClass.getDefaultDistances().stream()
                .map(distanceClass -> new CompetitionDTO(distanceClass.getInstance(), k)).collect(Collectors.toList());

        GraphBundle graphsForLearning = new GraphBundle(N, pIn, pOut, k, graphCountForLearning);
        GraphBundle graphsForCompetitions = new GraphBundle(N, pIn, pOut, k, graphCountForCompetitions);

        MetricCompetitionTask task = new MetricCompetitionTask(competitionDTOs, graphsForLearning, graphsForCompetitions, pointsCount, "test");
        task.execute().write();
    }
}

