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
import java.nio.file.Files;
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
        Integer N = 200;
        Double pIn = 0.4;
        Double pOut = 0.1;
        Integer k = 5;
        Integer countForLearning = 5;
        Integer countForCompetitions = 5;

        List<CompetitionDTO> competitionDTOs = DistanceClass.getDefaultDistances().stream()
                .map(distanceClass -> new CompetitionDTO(distanceClass.getInstance(), k)).collect(Collectors.toList());

        GraphBundle forLearning = new GraphBundle(N, pIn, pOut, k, countForLearning);
        GraphBundle forCompetitions = new GraphBundle(N, pIn, pOut, k, countForCompetitions);

        MetricCompetitionTask task = new MetricCompetitionTask(competitionDTOs, forLearning, forCompetitions, 150, "test");
        task.execute().write();
    }
}

