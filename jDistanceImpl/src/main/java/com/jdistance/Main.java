package com.jdistance;

import com.jdistance.adapter.generator.GraphBundle;
import com.jdistance.metric.DistanceClass;
import com.jdistance.workflow.Context;
import com.jdistance.workflow.task.competition.CompetitionDTO;
import com.jdistance.workflow.task.competition.MetricCompetitionTask;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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
        context.IMG_FOLDER = "pictures"; //"D:\\Dropbox\\jdistance\\pictures";
        context.COMPETITION_FOLDER = "tournament";
        context.CACHE_FOLDER = "cache";
        context.PARALLEL = true;
        context.USE_CACHE = false;
    }

    private static void callMetricCompetitionTask() {
        Integer N = 200;
        Double pIn = 0.4;
        Double pOut = 0.1;
        Integer k = 5;
        Integer count = 2;

        List<CompetitionDTO> competitionDTOs = DistanceClass.getDefaultDistances().stream()
                .map(distanceClass -> new CompetitionDTO(distanceClass.getInstance(), k)).collect(Collectors.toList());

        GraphBundle forLearning = new GraphBundle(N, pIn, pOut, k, count);
        GraphBundle forCompetitions = new GraphBundle(N, pIn, pOut, k, 1);

        MetricCompetitionTask task = new MetricCompetitionTask(competitionDTOs, forLearning, forCompetitions, 50, "test");
        task.execute().write();
    }
}

